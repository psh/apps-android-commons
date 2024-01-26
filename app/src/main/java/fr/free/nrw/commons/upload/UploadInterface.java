package fr.free.nrw.commons.upload;

import androidx.annotation.NonNull;
import com.google.gson.JsonObject;
import fr.free.nrw.commons.wikidata.WikidataConstants;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadInterface {

    @Multipart
    @POST(WikidataConstants.MW_API_PREFIX + "action=upload&stash=1&ignorewarnings=1")
    Observable<UploadResponse> uploadFileToStash(@Part("filename") RequestBody filename,
        @Part("filesize") RequestBody totalFileSize,
        @Part("offset") RequestBody offset,
        @Part("filekey") RequestBody fileKey,
        @Part("token") RequestBody token,
        @Part MultipartBody.Part filePart);

    @Headers("Cache-Control: no-cache")
    @POST(WikidataConstants.MW_API_PREFIX + "action=upload&ignorewarnings=1")
    @FormUrlEncoded
    @NonNull
    Observable<JsonObject> uploadFileFromStash(@NonNull @Field("token") String token,
        @NonNull @Field("text") String text,
        @NonNull @Field("comment") String comment,
        @NonNull @Field("filename") String filename,
        @NonNull @Field("filekey") String filekey);
}
