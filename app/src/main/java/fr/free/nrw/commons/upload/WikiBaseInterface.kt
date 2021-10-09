package fr.free.nrw.commons.upload

import io.reactivex.Observable
import org.wikipedia.dataclient.Service
import org.wikipedia.dataclient.mwapi.MwPostResponse
import org.wikipedia.dataclient.mwapi.MwQueryResponse
import retrofit2.http.*

/**
 * Retrofit calls for managing responses network calls of entity ids required for uploading depictions
 */
interface WikiBaseInterface {
    @Headers("Cache-Control: no-cache")
    @FormUrlEncoded
    @POST(Service.MW_API_PREFIX + "action=wbeditentity")
    fun postEditEntity(
        @Field("id") fileEntityId: String,
        @Field("token") editToken: String,
        @Field("data") data: String
    ): Observable<MwPostResponse>

    @GET(Service.MW_API_PREFIX + "action=query&prop=info")
    fun getFileEntityId(@Query("titles") fileName: String?): Observable<MwQueryResponse>

    /**
     * Upload Captions for the image when upload is successful
     *
     * @param fileEntityId enityId for the uploaded file
     * @param editToken editToken for the file
     * @param captionValue value of the caption to be uploaded
     */
    @FormUrlEncoded
    @POST(Service.MW_API_PREFIX + "action=wbsetlabel")
    fun addLabelstoWikidata(
        @Field("id") fileEntityId: String?,
        @Field("token") editToken: String?,
        @Field("language") language: String?,
        @Field("value") captionValue: String?
    ): Observable<MwPostResponse>
}