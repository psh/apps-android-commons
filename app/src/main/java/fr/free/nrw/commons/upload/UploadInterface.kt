package fr.free.nrw.commons.upload

import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.wikipedia.dataclient.Service
import retrofit2.http.*

interface UploadInterface {
    @Multipart
    @POST(Service.MW_API_PREFIX + "action=upload&stash=1&ignorewarnings=1")
    fun uploadFileToStash(
        @Part("filename") filename: RequestBody?,
        @Part("filesize") totalFileSize: RequestBody?,
        @Part("offset") offset: RequestBody?,
        @Part("filekey") fileKey: RequestBody?,
        @Part("token") token: RequestBody?,
        @Part filePart: MultipartBody.Part?
    ): Observable<UploadResponse>

    @Headers("Cache-Control: no-cache")
    @POST(Service.MW_API_PREFIX + "action=upload&ignorewarnings=1")
    @FormUrlEncoded
    fun uploadFileFromStash(
        @Field("token") token: String,
        @Field("text") text: String,
        @Field("comment") comment: String,
        @Field("filename") filename: String,
        @Field("filekey") filekey: String
    ): Observable<JsonObject>
}