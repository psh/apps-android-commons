package fr.free.nrw.commons.upload

import android.content.Context
import android.net.Uri
import fr.free.nrw.commons.Media
import fr.free.nrw.commons.auth.SessionManager
import fr.free.nrw.commons.contributions.Contribution
import fr.free.nrw.commons.filepicker.UploadableFile
import fr.free.nrw.commons.kvstore.JsonKvStore
import fr.free.nrw.commons.location.LatLng
import fr.free.nrw.commons.nearby.Place
import fr.free.nrw.commons.settings.Prefs
import fr.free.nrw.commons.upload.FileUtils.getSHA1
import fr.free.nrw.commons.upload.structure.depictions.DepictedItem
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UploadModel @Inject internal constructor(
    @param:Named("licenses") private val licenses: List<String>,
    @param:Named("default_preferences") private val store: JsonKvStore,
    @param:Named("licenses_by_name") private val licensesByName: Map<String, String>,
    private val context: Context,
    private val sessionManager: SessionManager,
    private val fileProcessor: FileProcessor,
    private val imageProcessingService: ImageProcessingService
) {
    private var license: String? = store.getString(Prefs.DEFAULT_LICENSE, Prefs.Licenses.CC_BY_SA_3)
    private val items: MutableList<UploadItem> = mutableListOf()
    private val compositeDisposable = CompositeDisposable()
    private val selectedCategories: MutableList<String> = mutableListOf()
    private val selectedDepictions: MutableList<DepictedItem> = mutableListOf()

    /**
     * Existing depicts which are selected
     */
    private var selectedExistingDepictions: MutableList<String> = mutableListOf()

    /**
     * cleanup the resources, I am Singleton, preparing for fresh upload
     */
    fun cleanUp() {
        compositeDisposable.clear()
        fileProcessor.cleanup()
        items.clear()
        selectedCategories.clear()
        selectedDepictions.clear()
        selectedExistingDepictions.clear()
    }

    fun setSelectedCategories(selectedCategories: List<String>) {
        this.selectedCategories.clear()
        this.selectedCategories.addAll(selectedCategories)
    }

    /**
     * pre process a one item at a time
     */
    fun preProcessImage(
        uploadableFile: UploadableFile,
        place: Place,
        similarImageInterface: SimilarImageInterface,
        inAppPictureLocation: LatLng
    ): Observable<UploadItem> {
        return Observable.just(
            createAndAddUploadItem(
                uploadableFile,
                place,
                similarImageInterface,
                inAppPictureLocation
            )
        )
    }

    /**
     * Calls validateImage() of ImageProcessingService to check quality of image
     *
     * @param uploadItem UploadItem whose quality is to be checked
     * @param inAppPictureLocation In app picture location (if any)
     * @return Quality of UploadItem
     */
    fun getImageQuality(uploadItem: UploadItem, inAppPictureLocation: LatLng?): Single<Int> =
        imageProcessingService.validateImage(uploadItem, inAppPictureLocation)

    /**
     * Calls checkDuplicateImage() of ImageProcessingService to check if image is duplicate
     *
     * @param filePath file to be checked
     * @return IMAGE_DUPLICATE or IMAGE_OK
     */
    fun checkDuplicateImage(filePath: String?): Single<Int> =
        imageProcessingService.checkDuplicateImage(filePath)

    /**
     * Calls validateCaption() of ImageProcessingService to check caption of image
     *
     * @param uploadItem UploadItem whose caption is to be checked
     * @return Quality of caption of the UploadItem
     */
    fun getCaptionQuality(uploadItem: UploadItem): Single<Int> =
        imageProcessingService.validateCaption(uploadItem)

    private fun createAndAddUploadItem(
        uploadableFile: UploadableFile,
        place: Place,
        similarImageInterface: SimilarImageInterface,
        inAppPictureLocation: LatLng
    ): UploadItem {
        val dateTimeWithSource = uploadableFile.getFileCreatedDate(context)
        var fileCreatedDate: Long = -1
        var createdTimestampSource = ""
        var fileCreatedDateString: String? = ""
        dateTimeWithSource?.let {
            fileCreatedDate = it.epochDate
            fileCreatedDateString = it.dateString
            createdTimestampSource = it.source
        }
        Timber.d("File created date is %d", fileCreatedDate)
        val imageCoordinates = fileProcessor.processFileCoordinates(
            similarImageInterface, uploadableFile.getFilePath(),
            inAppPictureLocation
        )
        val uploadItem = UploadItem(
            Uri.parse(uploadableFile.getFilePath()),
            uploadableFile.getMimeType(context)!!,
            imageCoordinates,
            place,
            fileCreatedDate,
            createdTimestampSource,
            uploadableFile.contentUri,
            fileCreatedDateString!!
        )

        // If an uploadItem of the same uploadableFile has been created before, we return that.
        // This is to avoid multiple instances of uploadItem of same file passed around.
        if (items.contains(uploadItem)) {
            return items[items.indexOf(uploadItem)]
        }

        uploadItem.getUploadMediaDetails()[0] = UploadMediaDetail(place)

        if (!items.contains(uploadItem)) {
            items.add(uploadItem)
        }
        return uploadItem
    }

    fun getCount(): Int = items.size

    fun getUploads(): List<UploadItem> = items

    fun getLicenses(): List<String> = licenses

    fun getSelectedLicense(): String? = license

    fun setSelectedLicense(licenseName: String?) {
        license = licensesByName[licenseName]
        if (licenseName != null) {
            store.putString(Prefs.DEFAULT_LICENSE, license!!)
        } else {
            store.remove(Prefs.DEFAULT_LICENSE)
        }
    }

    fun buildContributions(): Observable<Contribution> =
        Observable.fromIterable(items).map { item: UploadItem ->
                val imageSHA1 = getSHA1(
                    context.contentResolver.openInputStream(item.getContentUri())!!
                )
                val contribution = Contribution(
                    item,
                    sessionManager,
                    newListOf(selectedDepictions),
                    newListOf(selectedCategories),
                    imageSHA1
                )

                contribution.setHasInvalidLocation(item.hasInvalidLocation())

                Timber.d(
                    "Created timestamp while building contribution is %s, %s",
                    item.getCreatedTimestamp(),
                    Date(item.getCreatedTimestamp())
                )

                if (item.getCreatedTimestamp() != -1L) {
                    contribution.dateCreated = Date(item.getCreatedTimestamp())
                    contribution.dateCreatedSource = item.getCreatedTimestampSource()
                    //Set the date only if you have it, else the upload service is gonna try it the other way
                }

                if (contribution.wikidataPlace != null) {
                    if (item.isWLMUpload()) {
                        contribution.wikidataPlace!!.isMonumentUpload = true
                    } else {
                        contribution.wikidataPlace!!.isMonumentUpload = false
                    }
                }
                contribution.countryCode = item.getCountryCode()
                contribution
            }

    fun deletePicture(filePath: String) {
        val iterator = items.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().getMediaUri().toString().contains(filePath)) {
                iterator.remove()
                break
            }
        }
        if (items.isEmpty()) {
            cleanUp()
        }
    }

    fun getItems(): List<UploadItem> = items

    fun onDepictItemClicked(depictedItem: DepictedItem, media: Media?) {
        if (media == null) {
            if (depictedItem.isSelected) {
                selectedDepictions.add(depictedItem)
            } else {
                selectedDepictions.remove(depictedItem)
            }
        } else {
            if (depictedItem.isSelected) {
                if (media.depictionIds.contains(depictedItem.id)) {
                    selectedExistingDepictions.add(depictedItem.id)
                } else {
                    selectedDepictions.add(depictedItem)
                }
            } else {
                if (media.depictionIds.contains(depictedItem.id)) {
                    selectedExistingDepictions.remove(depictedItem.id)
                    if (!media.depictionIds.contains(depictedItem.id)) {
                        media.depictionIds = buildList {
                            add(depictedItem.id)
                            addAll(media.depictionIds)
                        }
                    }
                } else {
                    selectedDepictions.remove(depictedItem)
                }
            }
        }
    }

    private fun <T> newListOf(items: List<T>?): List<T> = buildList {
        items?.let { addAll(items) }
    }

    fun useSimilarPictureCoordinates(imageCoordinates: ImageCoordinates, uploadItemIndex: Int) {
        fileProcessor.prePopulateCategoriesAndDepictionsBy(imageCoordinates)
        items[uploadItemIndex].setGpsCoords(imageCoordinates)
    }

    fun getSelectedDepictions(): List<DepictedItem> = selectedDepictions

    fun getSelectedExistingDepictions(): List<String> = selectedExistingDepictions

    fun setSelectedExistingDepictions(selectedExistingDepictions: MutableList<String>) {
        this.selectedExistingDepictions = selectedExistingDepictions
    }
}
