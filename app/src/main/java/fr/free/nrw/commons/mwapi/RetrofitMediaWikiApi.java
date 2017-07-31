package fr.free.nrw.commons.mwapi;

import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.CookieManager;

import fr.free.nrw.commons.BuildConfig;
import fr.free.nrw.commons.mwapi.api.ApiService;
import fr.free.nrw.commons.mwapi.api.response.ApiResponse;
import fr.free.nrw.commons.mwapi.api.request.ClientLoginRequest;
import fr.free.nrw.commons.mwapi.api.request.LoginTokenQuery;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitMediaWikiApi {

    private static final String USER_AGENT = "Commons/"
            + BuildConfig.VERSION_NAME
            + " (https://mediawiki.org/wiki/Apps/Commons) Android/"
            + Build.VERSION.RELEASE;

    private Retrofit retrofit;
    private ApiService api;

    public RetrofitMediaWikiApi(String apiHost) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(chain ->
                        chain.proceed(chain.request()
                                .newBuilder()
                                .header("User-Agent", USER_AGENT).build()))
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .cookieJar(new JavaNetCookieJar(new CookieManager()))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(apiHost)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        api = retrofit.create(ApiService.class);
    }

    public String getLoginToken() {
        Call<ApiResponse> c = api.loginToken(new LoginTokenQuery());
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
        Call<ApiResponse> c = api.login(new ClientLoginRequest(loginToken, username, password));
        try {
            Response<ApiResponse> response = c.execute();
            ApiResponse body = response.body();
            if (body != null) {
                return body.clientlogin.status;
            }
        } catch (IOException e) {
            Log.e("MW", "Failed to login", e);
        }
        return null;
    }


}
