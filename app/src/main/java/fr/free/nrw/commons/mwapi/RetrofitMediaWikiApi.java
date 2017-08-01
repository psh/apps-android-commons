package fr.free.nrw.commons.mwapi;

import android.os.Build;

import java.io.IOException;
import java.net.CookieManager;
import java.util.Map;

import fr.free.nrw.commons.BuildConfig;
import fr.free.nrw.commons.mwapi.api.RequestBuilder;
import fr.free.nrw.commons.mwapi.api.ApiResponse;
import fr.free.nrw.commons.mwapi.api.LoginResponse;
import fr.free.nrw.commons.mwapi.api.QueryResponse;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

import static fr.free.nrw.commons.mwapi.api.RequestBuilder.action;

public class RetrofitMediaWikiApi {

    private static final String USER_AGENT = "Commons/"
            + BuildConfig.VERSION_NAME
            + " (https://mediawiki.org/wiki/Apps/Commons) Android/"
            + Build.VERSION.RELEASE;

    private ApiService api;

    public RetrofitMediaWikiApi(String apiHost) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .addNetworkInterceptor(chain ->
                        chain.proceed(chain.request()
                                .newBuilder()
                                .header("User-Agent", USER_AGENT).build()))
                .cookieJar(new JavaNetCookieJar(new CookieManager()));

        // Only enable logging when it's a debug build.
        if (BuildConfig.DEBUG) {
            httpClientBuilder.addNetworkInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        // We could get away with just using OkHttp but would have to write code to
        // encode the form encoded parameters and marshall the gson responses.
        // Retrofit takes care of all of that.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiHost)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClientBuilder.build())
                .build();

        api = retrofit.create(ApiService.class);
    }

    public String getLoginToken() {
        ApiResponse body = post(action("query")
                .param("type", "login")
                .param("meta", "tokens"));
        QueryResponse query = body != null ? body.query : null;
        QueryResponse.TokenResponse tokens = query != null ? query.tokens : null;
        return tokens != null ? tokens.loginToken : null;
    }

    public String login(String loginToken, String username, String password) {
        ApiResponse body = post(action("clientlogin")
                .param("loginreturnurl", "https://commons.wikimedia.org")
                .param("rememberMe", "1")
                .param("logintoken", loginToken)
                .param("username", username)
                .param("password", password));
        LoginResponse loginResponse = body != null ? body.clientlogin : null;
        if (loginResponse != null) {
            return loginResponse.getStatusCodeToReturn();
        }
        return null;
    }

    public boolean validateLogin() {
        ApiResponse body = get(action("query").param("meta", "userinfo"));
        QueryResponse query = body != null ? body.query : null;
        QueryResponse.UserInfoResponse userInfo = query != null ? query.userInfo : null;
        // note: may want to hold on to the username and the id for later.
        return userInfo != null && !userInfo.id.equals("0");
    }

    public String getEditToken() {
        ApiResponse body = get(action("query").param("meta", "tokens").param("type", "csrf"));
        QueryResponse query = body != null ? body.query : null;
        QueryResponse.TokenResponse tokens = query != null ? query.tokens : null;
        return tokens != null ? tokens.csrfToken : null;
    }





    private ApiResponse get(RequestBuilder requestBuilder) {
        try {
            Call<ApiResponse> apiResponseCall = api.remoteGetAction(requestBuilder.build());
            Response<ApiResponse> response = apiResponseCall.execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ApiResponse post(RequestBuilder requestBuilder) {
        try {
            Call<ApiResponse> apiResponseCall = api.remotePostAction(requestBuilder.build());
            Response<ApiResponse> response = apiResponseCall.execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface ApiService {
        @FormUrlEncoded
        @POST("/w/api.php")
        Call<ApiResponse> remotePostAction(@FieldMap Map<String, String> parameters);

        @GET("/w/api.php")
        Call<ApiResponse> remoteGetAction(@QueryMap Map<String, String> parameters);
    }
}
