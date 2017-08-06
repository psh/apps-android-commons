package fr.free.nrw.commons.mwapi.api;

import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MultipartBody;

public class RequestBuilder {

    private Map<String,String> params = new HashMap<>();
    private HttpUrl parsedApiHost;

    private RequestBuilder() {}

    public static RequestBuilder action(String action) {
        return new RequestBuilder()
                .param("format", "json")
                .param("action", action);
    }

    public RequestBuilder param(String name, String value) {
        params.put(name, value);
        return this;
    }

    public Map<String,String> build() {
        return params;
    }

    public MultipartBody buildMultipartBody() {
        MultipartBody.Builder body = new MultipartBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            body.addFormDataPart(entry.getKey(), entry.getValue());
        }
        body.setType(MultipartBody.FORM);
        return body.build();
    }

    public HttpUrl buildGetRequest(String apiHost) {
        parsedApiHost = HttpUrl.parse(apiHost);
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme(parsedApiHost.scheme())
                .host(parsedApiHost.host())
                .addPathSegment("w")
                .addPathSegment("api.php");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }
}
