package fr.free.nrw.commons.di;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import fr.free.nrw.commons.BetaConstants;
import fr.free.nrw.commons.BuildConfig;
import fr.free.nrw.commons.actions.PageEditClient;
import fr.free.nrw.commons.actions.PageEditInterface;
import fr.free.nrw.commons.actions.ThanksInterface;
import fr.free.nrw.commons.auth.SessionManager;
import fr.free.nrw.commons.auth.csrf.CsrfTokenClient;
import fr.free.nrw.commons.auth.csrf.CsrfTokenInterface;
import fr.free.nrw.commons.auth.login.LoginClient;
import fr.free.nrw.commons.auth.login.LoginInterface;
import fr.free.nrw.commons.category.CategoryInterface;
import fr.free.nrw.commons.explore.depictions.DepictsClient;
import fr.free.nrw.commons.kvstore.JsonKvStore;
import fr.free.nrw.commons.media.MediaDetailInterface;
import fr.free.nrw.commons.media.MediaInterface;
import fr.free.nrw.commons.media.PageMediaInterface;
import fr.free.nrw.commons.media.WikidataMediaInterface;
import fr.free.nrw.commons.mwapi.OkHttpJsonApiClient;
import fr.free.nrw.commons.mwapi.UserInterface;
import fr.free.nrw.commons.notification.NotificationInterface;
import fr.free.nrw.commons.review.ReviewInterface;
import fr.free.nrw.commons.upload.UploadInterface;
import fr.free.nrw.commons.upload.WikiBaseInterface;
import fr.free.nrw.commons.upload.depicts.DepictsInterface;
import fr.free.nrw.commons.wikidata.WikidataInterface;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.json.GsonUtil;
import timber.log.Timber;

@Module
@SuppressWarnings({"WeakerAccess", "unused"})
public class NetworkingModule {

    private static final String WIKIDATA_SPARQL_QUERY_URL = "https://query.wikidata.org/sparql";
    private static final String TOOLS_FORGE_URL = "https://tools.wmflabs.org/urbanecmbot/commonsmisc";

    private static final String TEST_TOOLS_FORGE_URL = "https://tools.wmflabs.org/commons-android-app/tool-commons-android-app";

    public static final long OK_HTTP_CACHE_SIZE = 10 * 1024 * 1024;

    private static final String NAMED_WIKI_DATA_WIKI_SITE = "wikidata-wikisite";
    private static final String NAMED_WIKI_PEDIA_WIKI_SITE = "wikipedia-wikisite";

    public static final String NAMED_LANGUAGE_WIKI_PEDIA_WIKI_SITE = "language-wikipedia-wikisite";

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(final Context context,
        final HttpLoggingInterceptor httpLoggingInterceptor) {
        final File dir = new File(context.getCacheDir(), "okHttpCache");
        return new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS).addInterceptor(httpLoggingInterceptor)
            .readTimeout(120, TimeUnit.SECONDS).cache(new Cache(dir, OK_HTTP_CACHE_SIZE)).build();
    }

    @Provides
    @Singleton
    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(
            message -> {
                Timber.tag("OkHttp").v(message);
            });
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.BASIC);
        return httpLoggingInterceptor;
    }

    @Provides
    @Singleton
    public OkHttpJsonApiClient provideOkHttpJsonApiClient(final OkHttpClient okHttpClient,
        final DepictsClient depictsClient, @Named("tools_forge") final HttpUrl toolsForgeUrl,
        @Named("test_tools_forge") final HttpUrl testToolsForgeUrl,
        @Named("default_preferences") final JsonKvStore defaultKvStore, final Gson gson) {
        return new OkHttpJsonApiClient(okHttpClient, depictsClient, toolsForgeUrl,
            testToolsForgeUrl, WIKIDATA_SPARQL_QUERY_URL, BuildConfig.WIKIMEDIA_CAMPAIGNS_URL,
            gson);
    }

    @Provides
    @Singleton
    public LoginClient provideLoginClient(final OkHttpClient httpClient) {
        return new LoginClient(CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL + "/",
            LoginInterface.class));
    }

    @Provides
    @Singleton
    public CsrfTokenClient provideCommonsCsrfTokenClient(final SessionManager sessionManager,
        final LoginClient loginClient, final OkHttpClient httpClient) {
        return new CsrfTokenClient(
            CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL, CsrfTokenInterface.class),
            sessionManager, loginClient
        );
    }

    @Provides
    @Named("wikimedia_api_host")
    @NonNull
    @SuppressWarnings("ConstantConditions")
    public String provideMwApiUrl() {
        return BuildConfig.WIKIMEDIA_API_HOST;
    }

    @Provides
    @Named("tools_forge")
    @NonNull
    @SuppressWarnings("ConstantConditions")
    public HttpUrl provideToolsForgeUrl() {
        return HttpUrl.parse(TOOLS_FORGE_URL);
    }

    @Provides
    @Named("test_tools_forge")
    @NonNull
    @SuppressWarnings("ConstantConditions")
    public HttpUrl provideTestToolsForgeUrl() {
        return HttpUrl.parse(TEST_TOOLS_FORGE_URL);
    }

    @Provides
    @Singleton
    @Named(NAMED_WIKI_DATA_WIKI_SITE)
    public WikiSite provideWikidataWikiSite() {
        return new WikiSite(BuildConfig.WIKIDATA_URL);
    }


    /**
     * Gson objects are very heavy. The app should ideally be using just one instance of it instead
     * of creating new instances everywhere.
     *
     * @return returns a singleton Gson instance
     */
    @Provides
    @Singleton
    public Gson provideGson() {
        return GsonUtil.getDefaultGson();
    }

    @Provides
    @Singleton
    public ReviewInterface provideReviewInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL,
            ReviewInterface.class);
    }

    @Provides
    @Singleton
    public DepictsInterface provideDepictsInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.WIKIDATA_URL,
            DepictsInterface.class);
    }

    @Provides
    @Singleton
    public WikiBaseInterface provideWikiBaseInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL,
            WikiBaseInterface.class);
    }

    @Provides
    @Singleton
    public UploadInterface provideUploadInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL,
            UploadInterface.class);
    }

    @Named("commons-page-edit-service")
    @Provides
    @Singleton
    public PageEditInterface providePageEditService(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL,
            PageEditInterface.class);
    }

    @Named("wikidata-page-edit-service")
    @Provides
    @Singleton
    public PageEditInterface provideWikiDataPageEditService(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.WIKIDATA_URL,
            PageEditInterface.class);
    }

    @Named("commons-page-edit")
    @Provides
    @Singleton
    public PageEditClient provideCommonsPageEditClient(final CsrfTokenClient csrfTokenClient,
        @Named("commons-page-edit-service") final PageEditInterface pageEditInterface) {
        return new PageEditClient(csrfTokenClient, pageEditInterface);
    }

    @Provides
    @Singleton
    public MediaInterface provideMediaInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL,
            MediaInterface.class);
    }

    /**
     * Add provider for WikidataMediaInterface It creates a retrofit service for the commons wiki
     * site
     *
     * @return WikidataMediaInterface
     */
    @Provides
    @Singleton
    public WikidataMediaInterface provideWikidataMediaInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BetaConstants.COMMONS_URL,
            WikidataMediaInterface.class);
    }

    @Provides
    @Singleton
    public MediaDetailInterface providesMediaDetailInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL,
            MediaDetailInterface.class);
    }

    @Provides
    @Singleton
    public CategoryInterface provideCategoryInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL,
            CategoryInterface.class);
    }

    @Provides
    @Singleton
    public ThanksInterface provideThanksInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL,
            ThanksInterface.class);
    }

    @Provides
    @Singleton
    public NotificationInterface provideNotificationInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL,
            NotificationInterface.class);
    }

    @Provides
    @Singleton
    public UserInterface provideUserInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.COMMONS_URL,
            UserInterface.class);
    }

    @Provides
    @Singleton
    public WikidataInterface provideWikidataInterface(final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, BuildConfig.WIKIDATA_URL,
            WikidataInterface.class);
    }

    /**
     * Add provider for PageMediaInterface It creates a retrofit service for the wiki site using
     * device's current language
     */
    @Provides
    @Singleton
    public PageMediaInterface providePageMediaInterface(
        @Named(NAMED_LANGUAGE_WIKI_PEDIA_WIKI_SITE) final WikiSite wikiSite,
        final OkHttpClient httpClient) {
        return CommonsServiceFactory.get(httpClient, wikiSite.url(), PageMediaInterface.class);
    }

    @Provides
    @Singleton
    @Named(NAMED_LANGUAGE_WIKI_PEDIA_WIKI_SITE)
    public WikiSite provideLanguageWikipediaSite() {
        return WikiSite.forLanguageCode(Locale.getDefault().getLanguage());
    }
}
