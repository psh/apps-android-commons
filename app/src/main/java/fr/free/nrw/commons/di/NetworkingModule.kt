package fr.free.nrw.commons.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import fr.free.nrw.commons.BetaConstants
import fr.free.nrw.commons.BuildConfig
import fr.free.nrw.commons.OkHttpConnectionFactory
import fr.free.nrw.commons.actions.PageEditClient
import fr.free.nrw.commons.actions.PageEditInterface
import fr.free.nrw.commons.actions.ThanksInterface
import fr.free.nrw.commons.auth.SessionManager
import fr.free.nrw.commons.auth.csrf.CsrfTokenClient
import fr.free.nrw.commons.auth.csrf.CsrfTokenInterface
import fr.free.nrw.commons.auth.csrf.LogoutClient
import fr.free.nrw.commons.auth.login.LoginClient
import fr.free.nrw.commons.auth.login.LoginInterface
import fr.free.nrw.commons.category.CategoryInterface
import fr.free.nrw.commons.explore.depictions.DepictsClient
import fr.free.nrw.commons.kvstore.JsonKvStore
import fr.free.nrw.commons.media.MediaDetailInterface
import fr.free.nrw.commons.media.MediaInterface
import fr.free.nrw.commons.media.PageMediaInterface
import fr.free.nrw.commons.media.WikidataMediaInterface
import fr.free.nrw.commons.mwapi.OkHttpJsonApiClient
import fr.free.nrw.commons.mwapi.UserInterface
import fr.free.nrw.commons.notification.NotificationInterface
import fr.free.nrw.commons.review.ReviewInterface
import fr.free.nrw.commons.upload.FileUtilsWrapper
import fr.free.nrw.commons.upload.PageContentsCreator
import fr.free.nrw.commons.upload.UploadClient
import fr.free.nrw.commons.upload.UploadInterface
import fr.free.nrw.commons.upload.WikiBaseInterface
import fr.free.nrw.commons.upload.depicts.DepictsInterface
import fr.free.nrw.commons.wikidata.CommonsServiceFactory
import fr.free.nrw.commons.wikidata.GsonUtil
import fr.free.nrw.commons.wikidata.WikidataInterface
import fr.free.nrw.commons.wikidata.cookies.CommonsCookieJar
import fr.free.nrw.commons.wikidata.cookies.CommonsCookieStorage
import fr.free.nrw.commons.wikidata.model.WikiSite
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@Suppress("unused")
class NetworkingModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        context: Context,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val dir = File(context.cacheDir, "okHttpCache")
        return OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .readTimeout(120, TimeUnit.SECONDS)
            .cache(Cache(dir, OK_HTTP_CACHE_SIZE))
            .build()
    }

    @Provides
    @Singleton
    fun serviceFactory(cookieJar: CommonsCookieJar?): CommonsServiceFactory {
        return CommonsServiceFactory(OkHttpConnectionFactory.getClient(cookieJar))
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor { message: String? ->
            Timber.tag("OkHttp").v(message)
        }
        httpLoggingInterceptor.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC)
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpJsonApiClient(
        okHttpClient: OkHttpClient?,
        depictsClient: DepictsClient?,
        @Named("tools_forge") toolsForgeUrl: HttpUrl?,
        @Named("default_preferences") defaultKvStore: JsonKvStore?,
        gson: Gson?
    ): OkHttpJsonApiClient {
        return OkHttpJsonApiClient(
            okHttpClient,
            depictsClient,
            toolsForgeUrl,
            WIKIDATA_SPARQL_QUERY_URL,
            BuildConfig.WIKIMEDIA_CAMPAIGNS_URL,
            gson
        )
    }

    @Provides
    @Singleton
    fun provideCookieStorage(
        @Named("default_preferences") preferences: JsonKvStore?
    ): CommonsCookieStorage {
        val cookieStorage = CommonsCookieStorage(preferences)
        cookieStorage.load()
        return cookieStorage
    }

    @Provides
    @Singleton
    fun provideCookieJar(storage: CommonsCookieStorage?): CommonsCookieJar {
        return CommonsCookieJar(storage!!)
    }

    @Named(NAMED_COMMONS_CSRF)
    @Provides
    @Singleton
    fun provideCommonsCsrfTokenClient(
        sessionManager: SessionManager?,
        tokenInterface: CsrfTokenInterface?, loginClient: LoginClient?, logoutClient: LogoutClient?
    ): CsrfTokenClient {
        return CsrfTokenClient(sessionManager!!, tokenInterface!!, loginClient!!, logoutClient!!)
    }

    @Provides
    @Singleton
    fun provideCsrfTokenInterface(serviceFactory: CommonsServiceFactory): CsrfTokenInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, CsrfTokenInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginInterface(serviceFactory: CommonsServiceFactory): LoginInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, LoginInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginClient(loginInterface: LoginInterface?): LoginClient {
        return LoginClient(loginInterface!!)
    }

    @Provides
    @Named("wikimedia_api_host")
    fun provideMwApiUrl(): String {
        return BuildConfig.WIKIMEDIA_API_HOST
    }

    @Provides
    @Named("tools_forge")
    fun provideToolsForgeUrl(): HttpUrl {
        return TOOLS_FORGE_URL.toHttpUrlOrNull()!!
    }

    @Provides
    @Singleton
    @Named(NAMED_WIKI_DATA_WIKI_SITE)
    fun provideWikidataWikiSite(): WikiSite {
        return WikiSite(BuildConfig.WIKIDATA_URL)
    }


    /**
     * Gson objects are very heavy. The app should ideally be using just one instance of it instead of creating new instances everywhere.
     * @return returns a singleton Gson instance
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonUtil.getDefaultGson()
    }

    @Provides
    @Singleton
    fun provideReviewInterface(serviceFactory: CommonsServiceFactory): ReviewInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, ReviewInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideDepictsInterface(serviceFactory: CommonsServiceFactory): DepictsInterface {
        return serviceFactory.create(BuildConfig.WIKIDATA_URL, DepictsInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideWikiBaseInterface(serviceFactory: CommonsServiceFactory): WikiBaseInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, WikiBaseInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideUploadInterface(serviceFactory: CommonsServiceFactory): UploadInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, UploadInterface::class.java)
    }

    @Named("commons-page-edit-service")
    @Provides
    @Singleton
    fun providePageEditService(serviceFactory: CommonsServiceFactory): PageEditInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, PageEditInterface::class.java)
    }

    @Named("wikidata-page-edit-service")
    @Provides
    @Singleton
    fun provideWikiDataPageEditService(serviceFactory: CommonsServiceFactory): PageEditInterface {
        return serviceFactory.create(BuildConfig.WIKIDATA_URL, PageEditInterface::class.java)
    }

    @Named("commons-page-edit")
    @Provides
    @Singleton
    fun provideCommonsPageEditClient(
        @Named(
            NAMED_COMMONS_CSRF
        ) csrfTokenClient: CsrfTokenClient?,
        @Named("commons-page-edit-service") pageEditInterface: PageEditInterface?
    ): PageEditClient {
        return PageEditClient(csrfTokenClient!!, pageEditInterface!!)
    }

    @Provides
    @Singleton
    fun provideMediaInterface(serviceFactory: CommonsServiceFactory): MediaInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, MediaInterface::class.java)
    }

    /**
     * Add provider for WikidataMediaInterface
     * It creates a retrofit service for the commons wiki site
     * @param commonsWikiSite commonsWikiSite
     * @return WikidataMediaInterface
     */
    @Provides
    @Singleton
    fun provideWikidataMediaInterface(serviceFactory: CommonsServiceFactory): WikidataMediaInterface {
        return serviceFactory.create(BetaConstants.COMMONS_URL, WikidataMediaInterface::class.java)
    }

    @Provides
    @Singleton
    fun providesMediaDetailInterface(serviceFactory: CommonsServiceFactory): MediaDetailInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, MediaDetailInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryInterface(serviceFactory: CommonsServiceFactory): CategoryInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, CategoryInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideThanksInterface(serviceFactory: CommonsServiceFactory): ThanksInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, ThanksInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationInterface(serviceFactory: CommonsServiceFactory): NotificationInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, NotificationInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideUserInterface(serviceFactory: CommonsServiceFactory): UserInterface {
        return serviceFactory.create(BuildConfig.COMMONS_URL, UserInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideWikidataInterface(serviceFactory: CommonsServiceFactory): WikidataInterface {
        return serviceFactory.create(BuildConfig.WIKIDATA_URL, WikidataInterface::class.java)
    }

    /**
     * Add provider for PageMediaInterface
     * It creates a retrofit service for the wiki site using device's current language
     */
    @Provides
    @Singleton
    fun providePageMediaInterface(
        @Named(NAMED_LANGUAGE_WIKI_PEDIA_WIKI_SITE) wikiSite: WikiSite,
        serviceFactory: CommonsServiceFactory
    ): PageMediaInterface {
        return serviceFactory.create(wikiSite.url(), PageMediaInterface::class.java)
    }

    @Provides
    @Singleton
    @Named(NAMED_LANGUAGE_WIKI_PEDIA_WIKI_SITE)
    fun provideLanguageWikipediaSite(): WikiSite {
        return WikiSite.forLanguageCode(Locale.getDefault().language)
    }

    @Provides
    fun provideUploadClient(
        uploadInterface: UploadInterface?,
        @Named(NetworkingModule.NAMED_COMMONS_CSRF) csrfTokenClient: CsrfTokenClient?,
        pageContentsCreator: PageContentsCreator?, fileUtilsWrapper: FileUtilsWrapper?,
        gson: Gson?
    ): UploadClient {
        return UploadClient(
            uploadInterface!!, csrfTokenClient!!, pageContentsCreator!!,
            fileUtilsWrapper!!, gson!!
        ) { System.currentTimeMillis() }
    }

    companion object {
        private const val WIKIDATA_SPARQL_QUERY_URL = "https://query.wikidata.org/sparql"
        private const val TOOLS_FORGE_URL =
            "https://tools.wmflabs.org/commons-android-app/tool-commons-android-app"

        const val OK_HTTP_CACHE_SIZE: Long = (10 * 1024 * 1024).toLong()

        private const val NAMED_WIKI_DATA_WIKI_SITE = "wikidata-wikisite"
        private const val NAMED_WIKI_PEDIA_WIKI_SITE = "wikipedia-wikisite"

        const val NAMED_LANGUAGE_WIKI_PEDIA_WIKI_SITE: String = "language-wikipedia-wikisite"

        const val NAMED_COMMONS_CSRF: String = "commons-csrf"
    }
}
