package fr.free.nrw.commons.mwapi.request;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class PostBuilder extends AbstractBuilder {
    PostBuilder(OkHttpClient okHttpClient, Gson gsonParser, HttpUrl parsedApiEndpoint) {
        super(okHttpClient, gsonParser, parsedApiEndpoint);
    }

    @Override
    protected Response getResponse() throws IOException {
        return okHttpClient.newCall(
                new Request.Builder()
                        .url(parsedApiEndpoint)
                        .post(buildMultipartBody())
                        .build()
        ).execute();
    }

    private MultipartBody buildMultipartBody() {
        MultipartBody.Builder body = new MultipartBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            body.addFormDataPart(entry.getKey(), entry.getValue());
        }
        body.setType(MultipartBody.FORM);
        return body.build();
    }
}
