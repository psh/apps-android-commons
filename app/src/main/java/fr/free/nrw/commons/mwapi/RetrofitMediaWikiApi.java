package fr.free.nrw.commons.mwapi;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import fr.free.nrw.commons.BuildConfig;
import fr.free.nrw.commons.mwapi.api.ApiResponse;
import fr.free.nrw.commons.mwapi.api.QueryResponse;
import fr.free.nrw.commons.mwapi.api.RequestBuilder;
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

@SuppressWarnings("ConstantConditions")
public class RetrofitMediaWikiApi implements MediaWikiApi {

    private static final String USER_AGENT = "Commons/"
            + BuildConfig.VERSION_NAME
            + " (https://mediawiki.org/wiki/Apps/Commons) Android/"
            + Build.VERSION.RELEASE;
    private final String apiHost;

    private ApiService api;
    private CookieManager cookieHandler;
    private JavaNetCookieJar cookieJar;

    public RetrofitMediaWikiApi(String apiHost) {
        this.apiHost = apiHost;
        cookieHandler = new CookieManager();
        cookieJar = new JavaNetCookieJar(cookieHandler);
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .addNetworkInterceptor(chain ->
                        chain.proceed(chain.request()
                                .newBuilder()
                                .header("User-Agent", USER_AGENT).build()))
                .cookieJar(cookieJar);

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

    @Override
    public String getAuthCookie() {
        List<HttpCookie> cookies = cookieHandler.getCookieStore().getCookies();
        StringBuilder sb = new StringBuilder();
        for (HttpCookie cookie : cookies) {
            sb.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
        }
        return sb.toString();
    }

    @Override
    public void setAuthCookie(String authCookie) {
        URI uri;
        try {
            uri = new URI(apiHost);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        String[] parts = authCookie.split(";");
        for (String cookieString : parts) {
            String[] cookieParts = cookieString.split("=");
            HttpCookie httpCookie = new HttpCookie(cookieParts[0], cookieParts[1]);
            httpCookie.setDomain(apiHost);
            cookieHandler.getCookieStore().add(uri, httpCookie);
        }
    }

    public String getLoginToken() {
        return post(action("query")
                .param("type", "login")
                .param("meta", "tokens"))
                .query.tokens.loginToken;
    }

    public String login(String loginToken, String username, String password) {
        return post(action("clientlogin")
                .param("loginreturnurl", "https://commons.wikimedia.org")
                .param("rememberMe", "1")
                .param("logintoken", loginToken)
                .param("username", username)
                .param("password", password))
                .clientlogin.getStatusCodeToReturn();
    }

    @Override
    public String login(String loginToken, String username, String password, String twoFactorCode) throws IOException {
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
        return get(action("query")
                .param("meta", "tokens")
                .param("type", "csrf"))
                .query.tokens.csrfToken;
    }

    @Override
    public boolean fileExistsWithName(String fileName) throws IOException {
        return false;
    }

    @Override
    public String findThumbnailByFilename(String filename) throws IOException {
        return null;
    }

    @Override
    public boolean logEvents(LogBuilder[] logBuilders) {
        return false;
    }

    @NonNull
    @Override
    public UploadResult uploadFile(String filename, InputStream file, long dataLength, String pageContents, String editSummary, ProgressListener progressListener) throws IOException {
        return new UploadResult("", "");
    }

    @Nullable
    @Override
    public String edit(String editToken, String processedPageContent, String filename, String summary) throws IOException {
        return "";
    }

    @NonNull
    @Override
    public MediaResult fetchMediaByFilename(String filename) throws IOException {
        return new MediaResult("", "");
    }

    @NonNull
    @Override
    public List<String> searchCategories(int searchCatsLimit, String filterValue) throws IOException {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public List<String> allCategories(int searchCatsLimit, String filter) throws IOException {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public List<String> searchTitles(int searchCatsLimit, String title) throws IOException {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public String revisionsByFilename(String filename) throws IOException {
        return null;
    }

    @Override
    public boolean existingFile(String fileSha1) throws IOException {
        return false;
    }

    @NonNull
    @Override
    public LogEventResult logEvents(String user, String lastModified, String queryContinue, int limit) throws IOException {
        return new LogEventResult(Collections.emptyList(), "");
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
