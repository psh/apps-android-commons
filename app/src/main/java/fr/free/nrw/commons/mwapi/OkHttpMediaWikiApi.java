package fr.free.nrw.commons.mwapi;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.free.nrw.commons.BuildConfig;
import fr.free.nrw.commons.Utils;
import fr.free.nrw.commons.mwapi.api.ApiResponse;
import fr.free.nrw.commons.mwapi.api.QueryResponse;
import fr.free.nrw.commons.mwapi.api.RequestBuilder;
import io.reactivex.Single;
import okhttp3.HttpUrl;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

import static fr.free.nrw.commons.mwapi.api.RequestBuilder.action;

@SuppressWarnings("ConstantConditions")
public class OkHttpMediaWikiApi implements MediaWikiApi {
    private static final String THUMB_SIZE = "640";
    private static final String CATEGORIES_NAMESPACE = "14";
    private static final String USER_AGENT = "Commons/"
            + BuildConfig.VERSION_NAME
            + " (https://mediawiki.org/wiki/Apps/Commons) Android/"
            + Build.VERSION.RELEASE;

    private final String apiHost;
    private final OkHttpClient okHttpClient;
    private final CookieManager cookieHandler;
    private final Gson gson;

    public OkHttpMediaWikiApi(String apiHost) {
        this.apiHost = apiHost;
        this.gson = new Gson();
        this.cookieHandler = new CookieManager();

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .addNetworkInterceptor(chain ->
                        chain.proceed(chain.request()
                                .newBuilder()
                                .header("User-Agent", USER_AGENT).build()))
                .cookieJar(new JavaNetCookieJar(cookieHandler));

        // Only enable logging when it's a debug build.
        if (BuildConfig.DEBUG) {
            httpClientBuilder.addNetworkInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        this.okHttpClient = httpClientBuilder.build();
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
        return post(action("clientlogin")
                .param("rememberMe", "1")
                .param("username", username)
                .param("password", password)
                .param("logintoken", loginToken)
                .param("logincontinue", "1")
                .param("OATHToken", twoFactorCode))
                .clientlogin.getStatusCodeToReturn(); // TODO
    }

    // note: may want to hold on to the username and the id for later.
    public boolean validateLogin() {
        return !get(action("query").param("meta", "userinfo"))
                .query.userInfo.id.equals("0");
    }

    public String getEditToken() {
        return get(action("query")
                .param("meta", "tokens")
                .param("type", "csrf"))
                .query.tokens.csrfToken;
    }

    @Override
    public boolean fileExistsWithName(String fileName) throws IOException {
        return get(action("query")
                .param("prop", "imageinfo")
                .param("titles", "File:" + fileName))
                .query.firstPage()
                .imageInfoCount() > 0;
    }

    @Override
    public String findThumbnailByFilename(String filename) throws IOException {
        return get(action("query")
                .param("prop", "imageinfo")
                .param("iiprop", "url")
                .param("iiurlwidth", THUMB_SIZE)
                .param("titles", filename))
                .query.firstPage().thumbUrl();
    }

    @Nullable
    @Override
    public String edit(String editToken, String processedPageContent, String filename, String summary) throws IOException {
        return post(action("edit")
                .param("title", filename)
                .param("token", editToken)
                .param("text", processedPageContent)
                .param("summary", summary))
                .edit.result;
    }

    @NonNull
    @Override
    public List<String> searchCategories(int searchCatsLimit, String filterValue) throws IOException {
        return get(action("query")
                .param("list", "search")
                .param("srwhat", "text")
                .param("srnamespace", CATEGORIES_NAMESPACE)
                .param("srlimit", ""+searchCatsLimit)
                .param("srsearch", filterValue))
                .query.categories();
    }

    @NonNull
    @Override
    public List<String> allCategories(int searchCatsLimit, String filter) throws IOException {
        return get(action("query")
                .param("list", "allcategories")
                .param("acprefix", filter)
                .param("aclimit", ""+searchCatsLimit))
                .query.allCategories();
    }

    @Override
    public boolean logEvents(LogBuilder[] logBuilders) {
        boolean allSuccess = true;
        for (LogBuilder logBuilder : logBuilders) {
            try {
                Response response = okHttpClient.newCall(
                        new Request.Builder()
                                .get()
                                .url(HttpUrl.parse(logBuilder.toUrlString()))
                                .build()
                ).execute();
                if (response.code() != 204) {
                    allSuccess = false;
                }
                Timber.d("EventLog hit %s", logBuilder.toUrlString());

            } catch (IOException e) {
                // Probably just ignore for now. Can be much more robust with a service, etc later on.
                Timber.d("IO Error, EventLog hit skipped");
            }
        }

        return allSuccess;
    }

    @NonNull
    @Override
    public MediaResult fetchMediaByFilename(String filename) throws IOException {
        String wikiContent = get(action("query")
                .param("prop", "revisions")
                .param("titles", filename)
                .param("rvprop", "content")
                .param("rvlimit", "1"))
                .query.firstPage().wikiContent();

        String renderedXml = post(action("parse")
                .param("title", "File:" + filename)
                .param("text", wikiContent)
                .param("prop", "parsetree")
                .param("contentformat", "text/x-wiki")
                .param("contentmodel", "wikitext"))
                .parsedContent();

        return new MediaResult(wikiContent, renderedXml);
    }

    @NonNull
    @Override
    // TODO:
    public UploadResult uploadFile(String filename, InputStream file, long dataLength, String pageContents, String editSummary, ProgressListener progressListener) throws IOException {
//        ApiResult result = api.upload(filename, file, dataLength, pageContents, editSummary, progressListener::onProgress);
//
//        Log.e("WTF", "Result: " + result.toString());
//
//        String resultStatus = result.getString("/api/upload/@result");
//        if (!resultStatus.equals("Success")) {
//            String errorCode = result.getString("/api/error/@code");
//            return new UploadResult(resultStatus, errorCode);
//        } else {
//            Date dateUploaded = Utils.parseMWDate(result.getString("/api/upload/imageinfo/@timestamp"));
//            String canonicalFilename = "File:" + result.getString("/api/upload/@filename").replace("_", " "); // Title vs Filename
//            String imageUrl = result.getString("/api/upload/imageinfo/@url");
//            return new UploadResult(resultStatus, dateUploaded, canonicalFilename, imageUrl);
//        }
        return new UploadResult("", "");
    }

    // TODO:
    @Nullable
    @Override
    public String revisionsByFilename(String filename) throws IOException {
        return ""
                /*api.action("query")
                .param("prop", "revisions")
                .param("rvprop", "timestamp|content")
                .param("titles", filename)
                .get()
                .getString("/api/query/pages/page/revisions/rev")*/;
    }

    // TODO:
    @Override
    public boolean existingFile(String fileSha1) throws IOException {
        return false; /*get(action("query")
                .param("list", "allimages")
                .param("aisha1", fileSha1))
                .getNodes("/api/query/allimages/img").size() > 0;*/
    }

    // TODO:
    @NonNull
    @Override
    public LogEventResult logEvents(String user, String lastModified, String queryContinue, int limit) throws IOException {
        RequestBuilder builder = action("query")
                .param("list", "logevents")
                .param("letype", "upload")
                .param("leprop", "title|timestamp|ids")
                .param("leuser", user)
                .param("lelimit", ""+limit);
        if (!TextUtils.isEmpty(lastModified)) {
            builder.param("leend", lastModified);
        }
        if (!TextUtils.isEmpty(queryContinue)) {
            builder.param("lestart", queryContinue);
        }
        ApiResponse result = get(builder);

        return new LogEventResult(getLogEventsFromResult(result), "");
    }

    // TODO
    @NonNull
    @Override
    public Single<Integer> getUploadCount(String userName) {
        return Single.fromCallable(() -> 0);
    }

    @NonNull
    private ArrayList<LogEventResult.LogEvent> getLogEventsFromResult(ApiResponse result) {
        List<QueryResponse.LogEventResponse> uploads = result.query != null
                ? result.query.logEvents : Collections.emptyList();
        ArrayList<LogEventResult.LogEvent> logEvents = new ArrayList<>();
        for (QueryResponse.LogEventResponse image : uploads) {
            logEvents.add(new LogEventResult.LogEvent(image.pageId, image.title,
                    Utils.parseMWDate(image.timestamp)));
        }
        return logEvents;
    }

    private ApiResponse get(RequestBuilder requestBuilder) {
        try {
            HttpUrl url = requestBuilder.buildGetRequest(apiHost);
            okhttp3.Call call = okHttpClient.newCall(new Request.Builder().url(url).get().build());
            okhttp3.Response response = call.execute();
            if (response.code() < 300) {
                String stream = response.body().string();
                Log.e("MW", "Response: " + stream);
                return gson.fromJson(stream, ApiResponse.class);
            }
        } catch (Exception e) {
            Log.e("MW", "Failed to GET", e);
        }
        return null;
    }

    private ApiResponse post(RequestBuilder requestBuilder) {
        try {
            okhttp3.Call call = okHttpClient.newCall(
                    new Request.Builder()
                            .url(HttpUrl.parse(apiHost + "w/api.php"))
                            .post(requestBuilder.buildMultipartBody())
                            .build()
            );
            okhttp3.Response response = call.execute();
            if (response.code() < 300) {
                String stream = response.body().string();
                Log.e("MW", "Response: " + stream);
                return gson.fromJson(stream, ApiResponse.class);
            }
        } catch (Exception e) {
            Log.e("MW", "Failed to POST", e);
        }
        return null;
    }
}
