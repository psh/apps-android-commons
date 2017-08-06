package fr.free.nrw.commons.mwapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Single;

/**
 * As the API calls are migrated from old / legacy code to new, the change can be made
 * in this file to reduce the scope of the change rippling out or a mix of old / new
 * code in the legacy <code>ApacheHttpClientMediaWikiApi</code>.
 */
public class MediaWikiApiFacade implements MediaWikiApi {
    private static final String API_HOST = "https://commons.wikimedia.org/";
    private static final String API_PATH = "w/api.php";

    private ApacheHttpClientMediaWikiApi legacy;
    private OkHttpMediaWikiApi newApi;

    @SuppressWarnings("deprecation")
    public MediaWikiApiFacade() {
        legacy = new ApacheHttpClientMediaWikiApi(API_HOST + API_PATH);
        newApi = new OkHttpMediaWikiApi(API_HOST);
    }

    @Override
    public String getAuthCookie() {
        return newApi.getAuthCookie();
    }

    @Override
    public void setAuthCookie(String authCookie) {
        newApi.setAuthCookie(authCookie);
    }

    @Override
    public String getLoginToken() throws IOException {
        return newApi.getLoginToken();
    }

    @Override
    public String login(String loginToken, String username, String password) throws IOException {
        return newApi.login(loginToken, username, password);
    }

    @Override
    public boolean validateLogin() throws IOException {
        return newApi.validateLogin();
    }

    @Override
    public String login(String loginToken, String username, String password, String twoFactorCode) throws IOException {
        return newApi.login(loginToken, username, password, twoFactorCode);
    }

    @Override
    public String getEditToken() throws IOException {
        return newApi.getEditToken();
    }

    @Override
    public boolean fileExistsWithName(String fileName) throws IOException {
        return newApi.fileExistsWithName(fileName);
    }

    @Override
    public String findThumbnailByFilename(String filename) throws IOException {
        return newApi.findThumbnailByFilename(filename);
    }

    @Override
    public boolean logEvents(LogBuilder[] logBuilders) {
        return newApi.logEvents(logBuilders);
    }

    @NonNull
    @Override
    public UploadResult uploadFile(String filename, InputStream file, long dataLength, String pageContents, String editSummary, ProgressListener progressListener) throws IOException {
        return newApi.uploadFile(filename, file, dataLength, pageContents, editSummary, progressListener);
    }

    @Nullable
    @Override
    public String edit(String editToken, String processedPageContent, String filename, String summary) throws IOException {
        return newApi.edit(editToken, processedPageContent, filename, summary);
    }

    @NonNull
    @Override
    public MediaResult fetchMediaByFilename(String filename) throws IOException {
        return newApi.fetchMediaByFilename(filename);
    }

    @NonNull
    @Override
    public List<String> searchCategories(int searchCatsLimit, String filterValue) throws IOException {
        return newApi.searchCategories(searchCatsLimit, filterValue);
    }

    @NonNull
    @Override
    public List<String> allCategories(int searchCatsLimit, String filter) throws IOException {
        return newApi.allCategories(searchCatsLimit, filter);
    }

    @Nullable
    @Override
    public String revisionsByFilename(String filename) throws IOException {
        return newApi.revisionsByFilename(filename);
    }

    @Override
    public boolean existingFile(String fileSha1) throws IOException {
        return newApi.existingFile(fileSha1);
    }

    @NonNull
    @Override
    public LogEventResult logEvents(String user, String lastModified, String queryContinue, int limit) throws IOException {
        return newApi.logEvents(user, lastModified, queryContinue, limit);
    }

    @NonNull
    @Override
    public Single<Integer> getUploadCount(String userName) {
        return legacy.getUploadCount(userName);
    }
}
