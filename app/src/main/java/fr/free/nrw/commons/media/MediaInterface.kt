package fr.free.nrw.commons.media

import io.reactivex.Single
import org.wikipedia.dataclient.mwapi.MwQueryResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Interface for interacting with Commons media related APIs
 */
interface MediaInterface {
    /**
     * Checks if a page exists or not.
     *
     * @param title the title of the page to be checked
     * @return
     */
    @GET("w/api.php?action=query&format=json&formatversion=2")
    fun checkPageExistsUsingTitle(@Query("titles") title: String?): Single<MwQueryResponse>

    /**
     * Check if file exists
     *
     * @param aisha1 the SHA of the media file to be checked
     * @return
     */
    @GET("w/api.php?action=query&format=json&formatversion=2&list=allimages")
    fun checkFileExistsUsingSha(@Query("aisha1") aisha1: String?): Single<MwQueryResponse>

    /**
     * This method retrieves a list of Media objects filtered using image generator query
     *
     * @param category     the category name. Must start with "Category:"
     * @param itemLimit    how many images are returned
     * @param continuation the continuation string from the previous query or empty map
     * @return
     */
    @GET(
        "w/api.php?action=query&format=json&formatversion=2"  //Basic parameters
                + "&generator=categorymembers&gcmtype=file&gcmsort=timestamp&gcmdir=desc"  //Category parameters
                + MEDIA_PARAMS
    )
    fun getMediaListFromCategory(
        @Query("gcmtitle") category: String?,
        @Query("gcmlimit") itemLimit: Int,
        @QueryMap continuation: Map<String, String>
    ): Single<MwQueryResponse>

    /**
     * This method retrieves a list of Media objects for a given user name
     *
     * @param username     user's Wikimedia Commons username.
     * @param itemLimit    how many images are returned
     * @param continuation the continuation string from the previous query or empty map
     * @return
     */
    @GET(
        "w/api.php?action=query&format=json&formatversion=2"  //Basic parameters
                + "&generator=allimages&gaisort=timestamp&gaidir=older"
                + MEDIA_PARAMS
    )
    fun getMediaListForUser(
        @Query("gaiuser") username: String?,
        @Query("gailimit") itemLimit: Int,
        @QueryMap(encoded = true) continuation: Map<String, String>
    ): Single<MwQueryResponse>

    /**
     * This method retrieves a list of Media objects filtered using image generator query
     *
     * @param keyword      the searched keyword
     * @param itemLimit    how many images are returned
     * @param offset       the offset in the result set
     * @return
     */
    @GET(
        "w/api.php?action=query&format=json&formatversion=2"  //Basic parameters
                + "&generator=search&gsrwhat=text&gsrnamespace=6"   //Search parameters
                + MEDIA_PARAMS
    )
    fun getMediaListFromSearch(
        @Query("gsrsearch") keyword: String?,
        @Query("gsrlimit") itemLimit: Int,
        @Query("gsroffset") offset: Int
    ): Single<MwQueryResponse>

    /**
     * Fetches Media object from the imageInfo API
     *
     * @param title       the tiles to be searched for. Can be filename or template name
     * @return
     */
    @GET(
        "w/api.php?action=query&format=json&formatversion=2"
                + MEDIA_PARAMS
    )
    fun getMedia(@Query("titles") title: String?): Single<MwQueryResponse>

    /**
     * Fetches Media object from the imageInfo API
     *
     * @param pageIds       the ids to be searched for
     * @return
     */
    @GET(
        "w/api.php?action=query&format=json&formatversion=2"
                + MEDIA_PARAMS
    )
    fun getMediaById(@Query("pageids") pageIds: String?): Single<MwQueryResponse>

    /**
     * Fetches Media object from the imageInfo API
     * Passes an image generator parameter
     *
     * @param title       the tiles to be searched for. Can be filename or template name
     * @return
     */
    @GET(
        "w/api.php?action=query&format=json&formatversion=2&generator=images"
                + MEDIA_PARAMS
    )
    fun getMediaWithGenerator(@Query("titles") title: String?): Single<MwQueryResponse>

    @GET("w/api.php?format=json&action=parse&prop=text")
    fun getPageHtml(@Query("page") title: String?): Single<MwParseResponse>

    /**
     * Fetches caption using file name
     *
     * @param filename name of the file to be used for fetching captions
     */
    @GET("w/api.php?action=wbgetentities&props=labels&format=json&languagefallback=1")
    fun fetchCaptionByFilename(
        @Query("language") language: String?,
        @Query("titles") filename: String?
    ): Single<MwQueryResponse>

    /**
     * Fetches list of images from a depiction entity
     * @param query depictionEntityId
     * @param srlimit the number of items to fetch
     * @param sroffset number od depictions already fetched, this is useful in implementing pagination
     */
    @GET(
        "w/api.php?action=query&format=json&formatversion=2"  //Basic parameters
                + "&generator=search&gsrnamespace=6"  //Search parameters
                + MEDIA_PARAMS
    )
    fun fetchImagesForDepictedItem(
        @Query("gsrsearch") query: String?,
        @Query("gsrlimit") srlimit: String?, @Query("gsroffset") sroffset: String?
    ): Single<MwQueryResponse>

    companion object {
        const val MEDIA_PARAMS = "&prop=imageinfo&iiprop=url|extmetadata|user&&iiurlwidth=640" +
                "&iiextmetadatafilter=DateTime|Categories|GPSLatitude|GPSLongitude|ImageDescription|DateTimeOriginal" +
                "|Artist|LicenseShortName|LicenseUrl"
    }
}