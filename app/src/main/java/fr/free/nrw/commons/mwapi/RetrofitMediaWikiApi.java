package fr.free.nrw.commons.mwapi;

import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.CookieManager;
import java.util.Map;

import fr.free.nrw.commons.BuildConfig;
import fr.free.nrw.commons.mwapi.api.RequestBuilder;
import fr.free.nrw.commons.mwapi.api.response.ApiResponse;
import fr.free.nrw.commons.mwapi.api.response.LoginResponse;
import fr.free.nrw.commons.mwapi.api.response.QueryResponse;
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
        Call<ApiResponse> c = post(action("query")
                .param("type", "login")
                .param("meta", "tokens"));
        try {
            Response<ApiResponse> response = c.execute();
            ApiResponse body = response.body();
            if (body != null) {
                return body.query.tokens.loginToken;
            }
        } catch (IOException e) {
            Log.e("MW", "Failed to get login token", e);
        }
        return null;
    }

    public String login(String loginToken, String username, String password) {
        Call<ApiResponse> c = post(action("clientlogin")
                .param("loginreturnurl", "https://commons.wikimedia.org")
                .param("rememberMe", "1")
                .param("logintoken", loginToken)
                .param("username", username)
                .param("password", password));
        try {
            Response<ApiResponse> response = c.execute();
            ApiResponse body = response.body();
            LoginResponse loginResponse = body != null ? body.clientlogin : null;
            if (loginResponse != null) {
                return loginResponse.getStatusCodeToReturn();
            }
        } catch (IOException e) {
            Log.e("MW", "Failed to login", e);
        }
        return null;
    }

    public boolean validateLogin() {
        Call<ApiResponse> c = get(action("query").param("meta", "userinfo"));
        try {
            Response<ApiResponse> response = c.execute();
            ApiResponse body = response.body();
            QueryResponse query = body != null ? body.query : null;
            QueryResponse.UserInfoResponse userInfo = query != null ? query.userInfo : null;
            // note: may want to hold on to the username and the id for later.
            return userInfo != null && !userInfo.id.equals("0");
        } catch (IOException e) {
            Log.e("MW", "Failed to login", e);
        }
        return false;
    }

    private Call<ApiResponse> get(RequestBuilder requestBuilder) {
        return api.remoteGetAction(requestBuilder.build());
    }

    private Call<ApiResponse> post(RequestBuilder requestBuilder) {
        return api.remotePostAction(requestBuilder.build());
    }

    public interface ApiService {
        @FormUrlEncoded
        @POST("/w/api.php")
        Call<ApiResponse> remotePostAction(@FieldMap Map<String, String> parameters);

        @FormUrlEncoded
        @GET("/w/api.php")
        Call<ApiResponse> remoteGetAction(@QueryMap Map<String, String> parameters);
    }
}
