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

    public static void use(OkHttpClient httpClient, Gson gson, String apiHost) {
        okHttpClient = httpClient;
        gsonParser = gson;
        parsedApiEndpoint = HttpUrl.parse(apiHost);
    }

    public static ActionBuilder post() {
        return new PostBuilder(okHttpClient, gsonParser, parsedApiEndpoint);
    }

    public static ActionBuilder get() {
        return new GetBuilder(okHttpClient, gsonParser, parsedApiEndpoint);
    }

    @SuppressWarnings("WeakerAccess")
    public interface ActionBuilder {
        ParameterBuilder action(String action);
    }

    @SuppressWarnings("WeakerAccess")
    public interface ParameterBuilder {
        ParameterBuilder param(String name, String value);

        ParameterBuilder param(String name, int value);

        ApiResponse execute();
    }
}
