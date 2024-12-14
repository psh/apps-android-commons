package fr.free.nrw.commons.upload

import android.net.Uri
import fr.free.nrw.commons.Utils
import fr.free.nrw.commons.filepicker.MimeTypeMapWrapper.Companion.getExtensionFromMimeType
import fr.free.nrw.commons.nearby.Place
import fr.free.nrw.commons.utils.ImageUtils
import io.reactivex.subjects.BehaviorSubject

class UploadItem(
    private var mediaUri: Uri,
    private val mimeType: String,
    private var gpsCoords: ImageCoordinates,
    private var place: Place,
    private val createdTimestamp: Long,
    private val createdTimestampSource: String,
    /**
     * Uri of uploadItem
     * Uri points to image location or name, eg content://media/external/images/camera/10495 (Android 10)
     */
    private var contentUri: Uri,
    //according to EXIF data
    private val fileCreatedDateString: String
) {
    private var uploadMediaDetails: MutableList<UploadMediaDetail>
    private val imageQuality: BehaviorSubject<Int>
    private var hasInvalidLocation = false
    private var isWLMUpload = false
    private var countryCode: String? = null

    init {
        uploadMediaDetails = ArrayList(listOf(UploadMediaDetail()))
        imageQuality = BehaviorSubject.createDefault(ImageUtils.IMAGE_WAIT)
    }

    fun getCreatedTimestampSource(): String = createdTimestampSource

    fun getGpsCoords(): ImageCoordinates = gpsCoords

    fun getUploadMediaDetails(): MutableList<UploadMediaDetail> = uploadMediaDetails

    fun getCreatedTimestamp(): Long = createdTimestamp

    fun getMediaUri(): Uri = mediaUri

    fun getImageQuality(): Int = imageQuality.value!!

    /**
     * getContentUri.
     * @return Uri of uploadItem
     * Uri points to image location or name, eg content://media/external/images/camera/10495 (Android 10)
     */
    fun getContentUri(): Uri = contentUri

    fun getFileCreatedDateString(): String = fileCreatedDateString

    fun setImageQuality(imageQuality: Int) {
        this.imageQuality.onNext(imageQuality)
    }

    /**
     * Sets the corresponding place to the uploadItem
     *
     * @param place geolocated Wikidata item
     */
    fun setPlace(place: Place) {
        this.place = place
    }

    fun getPlace(): Place = place

    fun setMediaDetails(uploadMediaDetails: MutableList<UploadMediaDetail>) {
        this.uploadMediaDetails = uploadMediaDetails
    }

    fun setWLMUpload(WLMUpload: Boolean) {
        isWLMUpload = WLMUpload
    }

    fun isWLMUpload(): Boolean = isWLMUpload

    override fun equals(obj: Any?): Boolean {
        if (obj !is UploadItem) {
            return false
        }
        return mediaUri.toString().contains((obj).mediaUri.toString())
    }

    override fun hashCode(): Int {
        return mediaUri.hashCode()
    }

    /**
     * Choose a filename for the media. Currently, the caption is used as a filename. If several
     * languages have been entered, the first language is used.
     */
    fun getFileName(): String = Utils.fixExtension(
        uploadMediaDetails[0].captionText,
        getExtensionFromMimeType(mimeType)
    )

    fun setGpsCoords(gpsCoords: ImageCoordinates) {
        this.gpsCoords = gpsCoords
    }

    fun setHasInvalidLocation(hasInvalidLocation: Boolean) {
        this.hasInvalidLocation = hasInvalidLocation
    }

    fun hasInvalidLocation(): Boolean = hasInvalidLocation

    fun setCountryCode(countryCode: String?) {
        this.countryCode = countryCode
    }

    fun getCountryCode(): String? = countryCode

    /**
     * Sets both the contentUri and mediaUri to the specified Uri.
     * This method allows you to assign the same Uri to both the contentUri and mediaUri
     * properties.
     *
     * @param uri The Uri to be set as both the contentUri and mediaUri.
     */
    fun setContentUri(uri: Uri) {
        contentUri = uri
        mediaUri = uri
    }
}
