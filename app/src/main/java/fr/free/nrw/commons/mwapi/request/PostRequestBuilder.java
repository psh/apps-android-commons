package fr.free.nrw.commons.mwapi.request;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;

import fr.free.nrw.commons.BuildConfig;
import fr.free.nrw.commons.mwapi.response.ApiResponse;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

class PostRequestBuilder extends AbstractRequestBuilder {
    private final OkHttpClient okHttpClient;
    private final Gson gsonParser;
    private final HttpUrl parsedApiEndpoint;

    PostRequestBuilder(OkHttpClient okHttpClient, Gson gsonParser, HttpUrl parsedApiEndpoint) {
        this.okHttpClient = okHttpClient;
        this.gsonParser = gsonParser;
        this.parsedApiEndpoint = parsedApiEndpoint;
    }

    @Override
    public ApiResponse execute() {
        try {
            okhttp3.Call call = okHttpClient.newCall(
                    new Request.Builder()
                            .url(parsedApiEndpoint)
                            .post(buildMultipartBody())
                            .build()
            );
            okhttp3.Response response = call.execute();
            if (response.code() < 300) {
                ResponseBody body = response.body();
                if (body == null) {
                    return null;
                }
                String stream = body.string();
                if (BuildConfig.DEBUG) {
                    Log.e("MW", "Response: " + stream);
                }
                return gsonParser.fromJson(stream, ApiResponse.class);
            }
        } catch (Exception e) {
            Log.e("MW", "Failed to POST", e);
        }
        return null;
    }

    MultipartBody buildMultipartBody() {
        MultipartBody.Builder body = new MultipartBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            body.addFormDataPart(entry.getKey(), entry.getValue());
        }
        body.setType(MultipartBody.FORM);
        return body.build();
    }
}
