package fr.free.nrw.commons.mwapi.request;

import com.google.gson.Gson;

import fr.free.nrw.commons.mwapi.response.ApiResponse;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class RequestBuilder {
    private static OkHttpClient okHttpClient;
    private static Gson gsonParser;
    private static HttpUrl parsedApiEndpoint;

    private RequestBuilder() {
    }

    public static ActionRequest post() {
        return new PostRequestBuilder(okHttpClient, gsonParser, parsedApiEndpoint);
    }

    public static ActionRequest get() {
        return new GetRequestBuilder(okHttpClient, gsonParser, parsedApiEndpoint);
    }

    public static void use(OkHttpClient httpClient, Gson gson, String apiHost) {
        okHttpClient = httpClient;
        gsonParser = gson;
        parsedApiEndpoint = HttpUrl.parse(apiHost);
    }

    @SuppressWarnings("WeakerAccess")
    public interface ActionRequest {
        ParameterBuilder action(String action);
    }

    @SuppressWarnings("WeakerAccess")
    public interface ParameterBuilder {
        ParameterBuilder param(String name, String value);

        ParameterBuilder param(String name, int value);

        ApiResponse execute();
    }
}
