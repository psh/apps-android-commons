package fr.free.nrw.commons.upload.mediaDetails

import android.app.Activity
import fr.free.nrw.commons.R
import fr.free.nrw.commons.di.CommonsApplicationModule
import fr.free.nrw.commons.filepicker.UploadableFile
import fr.free.nrw.commons.kvstore.BasicKvStore
import fr.free.nrw.commons.kvstore.JsonKvStore
import fr.free.nrw.commons.location.LatLng
import fr.free.nrw.commons.nearby.Place
import fr.free.nrw.commons.repository.UploadRepository
import fr.free.nrw.commons.upload.ImageCoordinates
import fr.free.nrw.commons.upload.SimilarImageInterface
import fr.free.nrw.commons.upload.UploadActivity
import fr.free.nrw.commons.upload.UploadItem
import fr.free.nrw.commons.upload.UploadMediaDetail
import fr.free.nrw.commons.upload.mediaDetails.UploadMediaDetailFragment.UploadMediaDetailFragmentCallback
import fr.free.nrw.commons.utils.DialogUtil.showAlertDialog
import fr.free.nrw.commons.utils.ImageUtils.EMPTY_CAPTION
import fr.free.nrw.commons.utils.ImageUtils.FILE_NAME_EXISTS
import fr.free.nrw.commons.utils.ImageUtils.IMAGE_KEEP
import fr.free.nrw.commons.utils.ImageUtils.IMAGE_OK
import fr.free.nrw.commons.utils.ImageUtils.getErrorMessageForResult
import io.github.coordinates2country.Coordinates2Country
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.apache.commons.lang3.StringUtils
import org.json.JSONObject
import timber.log.Timber
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.net.UnknownHostException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

class UploadMediaPresenter @Inject constructor(
    private val repository: UploadRepository,
    @param:Named("default_preferences") private val defaultKVStore: JsonKvStore,
    @param:Named(CommonsApplicationModule.IO_THREAD) private val ioScheduler: Scheduler,
    @param:Named(CommonsApplicationModule.MAIN_THREAD) private val mainThreadScheduler: Scheduler
) : UploadMediaDetailsContract.UserActionListener,
    SimilarImageInterface {
    private var view = DUMMY

    private val compositeDisposable = CompositeDisposable()

    private val WLM_SUPPORTED_COUNTRIES: List<String> = mutableListOf(
        "am",
        "at",
        "az",
        "br",
        "hr",
        "sv",
        "fi",
        "fr",
        "de",
        "gh",
        "in",
        "ie",
        "il",
        "mk",
        "my",
        "mt",
        "pk",
        "pe",
        "pl",
        "ru",
        "rw",
        "si",
        "es",
        "se",
        "tw",
        "ug",
        "ua",
        "us"
    )
    private var countryNamesAndCodes: Map<String, String>? = null

    private val keyForCurrentUploadImageQualities = "UploadedImagesQualities"

    override fun onAttachView(view: UploadMediaDetailsContract.View) {
        this.view = view
    }

    override fun onDetachView() {
        view = DUMMY
        compositeDisposable.clear()
    }

    /**
     * Sets the Upload Media Details for the corresponding upload item
     */
    override fun setUploadMediaDetails(
        uploadMediaDetails: List<UploadMediaDetail?>?,
        uploadItemIndex: Int
    ) = repository.getUploads()[uploadItemIndex].setMediaDetails(uploadMediaDetails)

    /**
     * Receives the corresponding uploadable file, processes it and return the view with and uplaod item
     */
    override fun receiveImage(
        uploadableFile: UploadableFile?, place: Place?, inAppPictureLocation: LatLng?
    ) {
        view.showProgress(true)
        compositeDisposable.add(
            repository
                .preProcessImage(uploadableFile, place, this, inAppPictureLocation)
                .map { uploadItem: UploadItem ->
                    if (place != null && place.isMonument) {
                        if (place.location != null) {
                            val countryCode = reverseGeoCode(place.location)
                            if (countryCode != null && WLM_SUPPORTED_COUNTRIES
                                    .contains(countryCode.lowercase())
                            ) {
                                uploadItem.isWLMUpload = true
                                uploadItem.countryCode = countryCode.lowercase()
                            }
                        }
                    }
                    uploadItem
                }
                .subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler)
                .subscribe(
                    { uploadItem: UploadItem ->
                        view.onImageProcessed(uploadItem, place)
                        view.updateMediaDetails(uploadItem.uploadMediaDetails)
                        view.showProgress(false)
                        val gpsCoords = uploadItem.gpsCoords
                        val hasImageCoordinates =
                            gpsCoords != null && gpsCoords.imageCoordsExists
                        if (hasImageCoordinates && place == null) {
                            checkNearbyPlaces(uploadItem)
                        }
                    },
                    { throwable: Throwable? ->
                        Timber.e(
                            throwable,
                            "Error occurred in processing images"
                        )
                    })
        )
    }

    private fun reverseGeoCode(latLng: LatLng): String? {
        if (countryNamesAndCodes == null) {
            countryNamesAndCodes = getCountryNamesAndCodes()
        }
        return countryNamesAndCodes!![Coordinates2Country.country(
            latLng.latitude,
            latLng.longitude
        )]
    }

    /**
     * Creates HashMap containing all ISO countries 2-letter codes provided by `Locale.getISOCountries()`
     * and their english names
     *
     * @return HashMap where Key is country english name and Value is 2-letter country code
     * e.g. ["Germany":"DE", ...]
     */
    private fun getCountryNamesAndCodes(): Map<String, String> {
        val result: MutableMap<String, String> = HashMap()

        val isoCountries = Locale.getISOCountries()

        for (isoCountry in isoCountries) {
            result[Locale("en", isoCountry).getDisplayCountry(Locale.ENGLISH)] =
                isoCountry
        }

        return result
    }

    /**
     * This method checks for the nearest location that needs images and suggests it to the user.
     */
    private fun checkNearbyPlaces(uploadItem: UploadItem) {
        val checkNearbyPlaces = Maybe.fromCallable {
            repository
                .checkNearbyPlaces(
                    uploadItem.gpsCoords.decLatitude,
                    uploadItem.gpsCoords.decLongitude
                )
        }
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
            .subscribe(
                { place: Place? ->
                    if (place != null) {
                        view.onNearbyPlaceFound(uploadItem, place)
                    }
                },
                { throwable: Throwable? ->
                    Timber.e(
                        throwable,
                        "Error occurred in processing images"
                    )
                })
        compositeDisposable.add(checkNearbyPlaces)
    }

    /**
     * Checks if the image has a location. Displays a dialog alerting user that no
     * location has been to added to the image and asking them to add one, if location was not
     * removed by the user
     *
     * @param uploadItemIndex Index of the uploadItem which has no location
     * @param inAppPictureLocation In app picture location (if any)
     * @param hasUserRemovedLocation True if user has removed location from the image
     */
    override fun displayLocDialog(
        uploadItemIndex: Int, inAppPictureLocation: LatLng?,
        hasUserRemovedLocation: Boolean
    ) {
        val uploadItems = repository.getUploads()
        val uploadItem = uploadItems[uploadItemIndex]
        if (uploadItem.gpsCoords.decimalCoords == null && inAppPictureLocation == null && !hasUserRemovedLocation) {
            val onSkipClicked = Runnable {
                verifyCaptionQuality(uploadItem)
            }
            view.displayAddLocationDialog(onSkipClicked)
        } else {
            verifyCaptionQuality(uploadItem)
        }
    }

    /**
     * Verifies the image's caption and calls function to handle the result
     *
     * @param uploadItem UploadItem whose caption is checked
     */
    private fun verifyCaptionQuality(uploadItem: UploadItem) {
        view.showProgress(true)
        compositeDisposable.add(
            repository
                .getCaptionQuality(uploadItem)
                .observeOn(mainThreadScheduler)
                .subscribe(
                    { capResult: Int ->
                        view.showProgress(false)
                        handleCaptionResult(capResult, uploadItem)
                    },
                    { throwable: Throwable ->
                        view.showProgress(false)
                        if (throwable is UnknownHostException) {
                            view.showConnectionErrorPopupForCaptionCheck()
                        } else {
                            view.showMessage(
                                "" + throwable.localizedMessage,
                                R.color.color_error
                            )
                        }
                        Timber.e(throwable, "Error occurred while handling image")
                    })
        )
    }

    /**
     * Handles image's caption results and shows dialog if necessary
     *
     * @param errorCode Error code of the UploadItem
     * @param uploadItem UploadItem whose caption is checked
     */
    fun handleCaptionResult(errorCode: Int, uploadItem: UploadItem?) {
        // If errorCode is empty caption show message
        if (errorCode == EMPTY_CAPTION) {
            Timber.d("Captions are empty. Showing toast")
            view.showMessage(R.string.add_caption_toast, R.color.color_error)
        }

        // If image with same file name exists check the bit in errorCode is set or not
        if ((errorCode and FILE_NAME_EXISTS) != 0) {
            Timber.d("Trying to show duplicate picture popup")
            view.showDuplicatePicturePopup(uploadItem)
        }

        // If caption is not duplicate or user still wants to upload it
        if (errorCode == IMAGE_OK) {
            Timber.d("Image captions are okay or user still wants to upload it")
            view.onImageValidationSuccess()
        }
    }


    /**
     * Copies the caption and description of the current item to the subsequent media
     */
    override fun copyTitleAndDescriptionToSubsequentMedia(indexInViewFlipper: Int) {
        for (i in indexInViewFlipper + 1 until repository.getCount()) {
            val subsequentUploadItem = repository.getUploads()[i]
            subsequentUploadItem.setMediaDetails(deepCopy(repository.getUploads()[indexInViewFlipper].uploadMediaDetails))
        }
    }

    /**
     * Fetches and set the caption and description of the item
     */
    override fun fetchTitleAndDescription(indexInViewFlipper: Int) {
        val currentUploadItem = repository.getUploads()[indexInViewFlipper]
        view.updateMediaDetails(currentUploadItem.uploadMediaDetails)
    }

    private fun deepCopy(uploadMediaDetails: List<UploadMediaDetail>): List<UploadMediaDetail> {
        val newList = ArrayList<UploadMediaDetail>()
        for (uploadMediaDetail in uploadMediaDetails) {
            newList.add(uploadMediaDetail.javaCopy())
        }
        return newList
    }

    override fun useSimilarPictureCoordinates(
        imageCoordinates: ImageCoordinates,
        uploadItemIndex: Int
    ) {
        repository.useSimilarPictureCoordinates(imageCoordinates, uploadItemIndex)
    }

    override fun onMapIconClicked(indexInViewFlipper: Int) {
        view.showExternalMap(repository.getUploads()[indexInViewFlipper])
    }

    override fun onEditButtonClicked(indexInViewFlipper: Int) {
        view.showEditActivity(repository.getUploads()[indexInViewFlipper])
    }

    /**
     * Updates the information regarding the specified place for uploads
     * when the user confirms the suggested nearby place.
     *
     * @param place The place to be associated with the uploads.
     */
    override fun onUserConfirmedUploadIsOfPlace(place: Place) {
        val uploads = repository.getUploads()
        for (uploadItem in uploads) {
            uploadItem.place = place
            val uploadMediaDetails = uploadItem.uploadMediaDetails
            // Update UploadMediaDetail object for this UploadItem
            uploadMediaDetails[0] = UploadMediaDetail(place)
        }
        // Now that all UploadItems and their associated UploadMediaDetail objects have been updated,
        // update the view with the modified media details of the first upload item
        view.updateMediaDetails(uploads[0].uploadMediaDetails)
        UploadActivity.setUploadIsOfAPlace(true)
    }


    /**
     * Calculates the image quality
     *
     * @param uploadItemIndex      Index of the UploadItem whose quality is to be checked
     * @param inAppPictureLocation In app picture location (if any)
     * @param activity             Context reference
     * @return true if no internal error occurs, else returns false
     */
    override fun getImageQuality(
        uploadItemIndex: Int,
        inAppPictureLocation: LatLng,
        activity: Activity
    ): Boolean {
        val uploadItems = repository.getUploads()
        view.showProgress(true)
        if (uploadItems.isEmpty()) {
            view.showProgress(false)
            // No internationalization required for this error message because it's an internal error.
            view.showMessage(
                "Internal error: Zero upload items received by the Upload Media Detail Fragment. Sorry, please upload again.",
                R.color.color_error
            )
            return false
        }
        val uploadItem = uploadItems[uploadItemIndex]
        compositeDisposable.add(
            repository
                .getImageQuality(uploadItem, inAppPictureLocation)
                .observeOn(mainThreadScheduler)
                .subscribe(
                    { imageResult: Int ->
                        storeImageQuality(imageResult, uploadItemIndex, activity, uploadItem)
                    },
                    { throwable: Throwable ->
                        if (throwable is UnknownHostException) {
                            view.showProgress(false)
                            view.showConnectionErrorPopup()
                        } else {
                            view.showMessage(
                                "" + throwable.localizedMessage,
                                R.color.color_error
                            )
                        }
                        Timber.e(throwable, "Error occurred while handling image")
                    })
        )
        return true
    }

    /**
     * Stores the image quality in JSON format in SharedPrefs
     *
     * @param imageResult     Image quality
     * @param uploadItemIndex Index of the UploadItem whose quality is calculated
     * @param activity        Context reference
     * @param uploadItem      UploadItem whose quality is to be checked
     */
    private fun storeImageQuality(
        imageResult: Int, uploadItemIndex: Int, activity: Activity,
        uploadItem: UploadItem
    ) {
        val store = BasicKvStore(
            activity,
            UploadActivity.storeNameForCurrentUploadImagesSize
        )
        val value = store.getString(keyForCurrentUploadImageQualities, null)
        val jsonObject: JSONObject
        try {
            jsonObject = if (value != null) {
                JSONObject(value)
            } else {
                JSONObject()
            }
            jsonObject.put("UploadItem$uploadItemIndex", imageResult)
            store.putString(keyForCurrentUploadImageQualities, jsonObject.toString())
        } catch (e: Exception) {
            Timber.e(e)
        }

        if (uploadItemIndex == 0) {
            if (!isBatteryDialogShowing && !isCategoriesDialogShowing) {
                // if battery-optimisation dialog is not being shown, call checkImageQuality
                checkImageQuality(uploadItem, uploadItemIndex)
            } else {
                view.showProgress(false)
            }
        }
    }

    /**
     * Used to check image quality from stored qualities and display dialogs
     *
     * @param uploadItem UploadItem whose quality is to be checked
     * @param index      Index of the UploadItem whose quality is to be checked
     */
    override fun checkImageQuality(uploadItem: UploadItem, index: Int) {
        if ((uploadItem.imageQuality != IMAGE_OK) && (uploadItem.imageQuality != IMAGE_KEEP)) {
            val store = BasicKvStore(
                UploadMediaDetailFragment.activity,
                UploadActivity.storeNameForCurrentUploadImagesSize
            )
            val value = store.getString(keyForCurrentUploadImageQualities, null)
            val jsonObject: JSONObject
            try {
                jsonObject = if (value != null) {
                    JSONObject(value)
                } else {
                    JSONObject()
                }
                val imageQuality = jsonObject["UploadItem$index"] as Int
                view.showProgress(false)
                if (imageQuality == IMAGE_OK) {
                    uploadItem.setHasInvalidLocation(false)
                    uploadItem.imageQuality = imageQuality
                } else {
                    handleBadImage(imageQuality, uploadItem, index)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    /**
     * Updates the image qualities stored in JSON, whenever an image is deleted
     *
     * @param size Size of uploadableFiles
     * @param index Index of the UploadItem which was deleted
     */
    override fun updateImageQualitiesJSON(size: Int, index: Int) {
        val store = BasicKvStore(
            UploadMediaDetailFragment.activity,
            UploadActivity.storeNameForCurrentUploadImagesSize
        )
        val value = store.getString(keyForCurrentUploadImageQualities, null)
        val jsonObject: JSONObject
        try {
            jsonObject = if (value != null) {
                JSONObject(value)
            } else {
                JSONObject()
            }
            for (i in index until (size - 1)) {
                jsonObject.put("UploadItem$i", jsonObject["UploadItem" + (i + 1)])
            }
            jsonObject.remove("UploadItem" + (size - 1))
            store.putString(keyForCurrentUploadImageQualities, jsonObject.toString())
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * Handles bad pictures, like too dark, already on wikimedia, downloaded from internet
     *
     * @param errorCode Error code of the bad image quality
     * @param uploadItem UploadItem whose quality is bad
     * @param index Index of item whose quality is bad
     */
    fun handleBadImage(
        errorCode: Int,
        uploadItem: UploadItem, index: Int
    ) {
        Timber.d("Handle bad picture with error code %d", errorCode)
        if (errorCode >= 8) { // If location of image and nearby does not match
            uploadItem.setHasInvalidLocation(true)
        }

        // If image has some other problems, show popup accordingly
        if (errorCode != EMPTY_CAPTION && errorCode != FILE_NAME_EXISTS) {
            showBadImagePopup(errorCode, index, UploadMediaDetailFragment.activity, uploadItem)
        }
    }

    /**
     * Shows a dialog describing the potential problems in the current image
     *
     * @param errorCode  Has the potential problems in the current image
     * @param index      Index of the UploadItem which has problems
     * @param activity   Context reference
     * @param uploadItem UploadItem which has problems
     */
    fun showBadImagePopup(
        errorCode: Int,
        index: Int, activity: Activity, uploadItem: UploadItem
    ) {
        val errorMessageForResult = getErrorMessageForResult(activity, errorCode)
        if (!StringUtils.isBlank(errorMessageForResult)) {
            showAlertDialog(activity,
                activity.getString(R.string.upload_problem_image),
                errorMessageForResult,
                activity.getString(R.string.upload),
                activity.getString(R.string.cancel),
                {
                    view.showProgress(false)
                    uploadItem.imageQuality = IMAGE_OK
                },
                {
                    presenterCallback!!.deletePictureAtIndex(index)
                }
            )!!.setCancelable(false)
        }
        //If the error message is null, we will probably not show anything
    }

    /**
     * notifies the user that a similar image exists
     */
    override fun showSimilarImageFragment(
        originalFilePath: String?, possibleFilePath: String?,
        similarImageCoordinates: ImageCoordinates?
    ) {
        view.showSimilarImageFragment(
            originalFilePath, possibleFilePath,
            similarImageCoordinates
        )
    }

    companion object {
        private val DUMMY = Proxy
            .newProxyInstance(
                UploadMediaDetailsContract.View::class.java.classLoader,
                arrayOf<Class<*>>(UploadMediaDetailsContract.View::class.java)
            ) { proxy: Any?, method: Method?, methodArgs: Array<Any?>? -> null } as UploadMediaDetailsContract.View

        @JvmField
        var presenterCallback: UploadMediaDetailFragmentCallback? = null

        /**
         * Variable used to determine if the battery-optimisation dialog is being shown or not
         */
        @JvmField
        var isBatteryDialogShowing: Boolean = false

        @JvmField
        var isCategoriesDialogShowing: Boolean = false
    }
}
