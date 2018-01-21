package fr.free.nrw.commons.mwapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonMediaWikiAppi implements MediaWikiApi {
    private static final Type MAP = new TypeToken<Map<String, Object>>(){}.getType();
    private final OkHttpClient client;
    private final HttpUrl apiEndpoint;

    public JsonMediaWikiAppi(String apiEndpoint) {
        this.client = new OkHttpClient.Builder().build();
        this.apiEndpoint = HttpUrl.parse(apiEndpoint);
    }

    public void captcha() {
        String token = getToken("createaccount", "createaccounttoken");
        Map<String, Object> fields = fields();
        Log.e("", token);
    }

    private Map<String, Object> fields() {
        try {
            Response result = client.newCall(new Request.Builder().get().url(
                    apiEndpoint.newBuilder()
                            .addQueryParameter("action", "query")
                            .addQueryParameter("format", "json")
                            .addQueryParameter("meta", "authmanagerinfo")
                            .addQueryParameter("amirequestsfor", "create")
                            .addQueryParameter("amimessageformat", "raw").build()
            ).build()).execute();
            return new Gson().fromJson(result.body().charStream(), MAP);
        } catch (Exception e) {
            return null;
        }
    }

    private String getToken(String type, String queryKey) {
        String token="";
        try {
            Response result = client.newCall(new Request.Builder().get().url(
                    apiEndpoint.newBuilder()
                            .addQueryParameter("action", "query")
                            .addQueryParameter("format", "json")
                            .addQueryParameter("meta", "tokens")
                            .addQueryParameter("type", type).build()
            ).build()).execute();
            Map<String, Object> data = new Gson().fromJson(result.body().charStream(), MAP);
            token = (String) step(step(data, "query"), "tokens").get(queryKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

    private Map<String, Object> step(Map<String, Object> data, String key) {
        return (Map<String, Object>) data.get(key);
    }

    @Override
    public String getAuthCookie() {
        return null;
    }

    @Override
    public void setAuthCookie(String authCookie) {

    }

    @Override
    public String login(String username, String password) throws IOException {
        return null;
    }

    @Override
    public String login(String username, String password, String twoFactorCode) throws IOException {
        return null;
    }

    @Override
    public boolean validateLogin() throws IOException {
        return false;
    }

    @Override
    public String getEditToken() throws IOException {
        return null;
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
        return null;
    }

    @Nullable
    @Override
    public String edit(String editToken, String processedPageContent, String filename, String summary) throws IOException {
        return null;
    }

    @NonNull
    @Override
    public MediaResult fetchMediaByFilename(String filename) throws IOException {
        return null;
    }

    @NonNull
    @Override
    public Observable<String> searchCategories(String filterValue, int searchCatsLimit) {
        return null;
    }

    @NonNull
    @Override
    public Observable<String> allCategories(String filter, int searchCatsLimit) {
        return null;
    }

    @NonNull
    @Override
    public Observable<String> searchTitles(String title, int searchCatsLimit) {
        return null;
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
        return null;
    }

    @NonNull
    @Override
    public Single<Integer> getUploadCount(String userName) {
        return null;
    }
}
