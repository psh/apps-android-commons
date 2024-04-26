package fr.free.nrw.commons.di

import android.app.Activity
import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.collection.LruCache
import androidx.room.Room.databaseBuilder
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import fr.free.nrw.commons.BuildConfig
import fr.free.nrw.commons.R
import fr.free.nrw.commons.auth.AccountUtil
import fr.free.nrw.commons.auth.SessionManager
import fr.free.nrw.commons.contributions.ContributionDao
import fr.free.nrw.commons.customselector.database.NotForUploadStatusDao
import fr.free.nrw.commons.customselector.database.UploadedStatusDao
import fr.free.nrw.commons.customselector.ui.selector.ImageFileLoader
import fr.free.nrw.commons.data.DBOpenHelper
import fr.free.nrw.commons.db.AppDatabase
import fr.free.nrw.commons.kvstore.JsonKvStore
import fr.free.nrw.commons.location.LocationServiceManager
import fr.free.nrw.commons.review.ReviewDao
import fr.free.nrw.commons.settings.Prefs
import fr.free.nrw.commons.upload.UploadController
import fr.free.nrw.commons.upload.depicts.DepictsDao
import fr.free.nrw.commons.utils.ConfigUtils.isBetaFlavour
import fr.free.nrw.commons.wikidata.WikidataEditListener
import fr.free.nrw.commons.wikidata.WikidataEditListenerImpl
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Objects
import javax.inject.Named
import javax.inject.Singleton

/**
 * The Dependency Provider class for Commons Android.
 *
 * Provides all sorts of ContentProviderClients used by the app
 * along with the Liscences, AccountUtility, UploadController, Logged User,
 * Location manager etc
 */
@Module
@Suppress("unused")
open class CommonsApplicationModule(private val applicationContext: Context) {
    private var appDatabase: AppDatabase? = null

    /**
     * Provides ImageFileLoader used to fetch device images.
     * @param context
     * @return
     */
    @Provides
    fun providesImageFileLoader(context: Context?): ImageFileLoader {
        return ImageFileLoader(context!!)
    }

    @Provides
    fun providesApplicationContext(): Context {
        return this.applicationContext
    }

    @Provides
    fun provideInputMethodManager(): InputMethodManager {
        return applicationContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    @Provides
    @Named("licenses")
    fun provideLicenses(context: Context): List<String> {
        val licenseItems: MutableList<String> = ArrayList()
        licenseItems.add(context.getString(R.string.license_name_cc0))
        licenseItems.add(context.getString(R.string.license_name_cc_by))
        licenseItems.add(context.getString(R.string.license_name_cc_by_sa))
        licenseItems.add(context.getString(R.string.license_name_cc_by_four))
        licenseItems.add(context.getString(R.string.license_name_cc_by_sa_four))
        return licenseItems
    }

    @Provides
    @Named("licenses_by_name")
    fun provideLicensesByName(context: Context): Map<String, String> {
        val byName: MutableMap<String, String> = HashMap()
        byName[context.getString(R.string.license_name_cc0)] = Prefs.Licenses.CC0
        byName[context.getString(R.string.license_name_cc_by)] = Prefs.Licenses.CC_BY_3
        byName[context.getString(R.string.license_name_cc_by_sa)] =
            Prefs.Licenses.CC_BY_SA_3
        byName[context.getString(R.string.license_name_cc_by_four)] = Prefs.Licenses.CC_BY_4
        byName[context.getString(R.string.license_name_cc_by_sa_four)] = Prefs.Licenses.CC_BY_SA_4
        return byName
    }

    @Provides
    open fun providesAccountUtil(context: Context?): AccountUtil {
        return AccountUtil()
    }

    /**
     * Provides an instance of CategoryContentProviderClient i.e. the categories
     * that are there in local storage
     */
    @Provides
    @Named("category")
    open fun provideCategoryContentProviderClient(context: Context): ContentProviderClient {
        return context.contentResolver.acquireContentProviderClient(BuildConfig.CATEGORY_AUTHORITY)!!
    }

    /**
     * This method is used to provide instance of RecentSearchContentProviderClient
     * which provides content of Recent Searches from database
     * @param context
     * @return returns RecentSearchContentProviderClient
     */
    @Provides
    @Named("recentsearch")
    fun provideRecentSearchContentProviderClient(context: Context): ContentProviderClient {
        return context.contentResolver.acquireContentProviderClient(BuildConfig.RECENT_SEARCH_AUTHORITY)!!
    }

    @Provides
    @Named("contribution")
    open fun provideContributionContentProviderClient(context: Context): ContentProviderClient {
        return context.contentResolver.acquireContentProviderClient(BuildConfig.CONTRIBUTION_AUTHORITY)!!
    }

    @Provides
    @Named("modification")
    open fun provideModificationContentProviderClient(context: Context): ContentProviderClient {
        return context.contentResolver.acquireContentProviderClient(BuildConfig.MODIFICATION_AUTHORITY)!!
    }

    @Provides
    @Named("bookmarks")
    fun provideBookmarkContentProviderClient(context: Context): ContentProviderClient {
        return context.contentResolver.acquireContentProviderClient(BuildConfig.BOOKMARK_AUTHORITY)!!
    }

    @Provides
    @Named("bookmarksLocation")
    fun provideBookmarkLocationContentProviderClient(context: Context): ContentProviderClient {
        return context.contentResolver.acquireContentProviderClient(BuildConfig.BOOKMARK_LOCATIONS_AUTHORITY)!!
    }

    @Provides
    @Named("bookmarksItem")
    fun provideBookmarkItemContentProviderClient(context: Context): ContentProviderClient {
        return context.contentResolver.acquireContentProviderClient(BuildConfig.BOOKMARK_ITEMS_AUTHORITY)!!
    }

    /**
     * This method is used to provide instance of RecentLanguagesContentProvider
     * which provides content of recent used languages from database
     * @param context Context
     * @return returns RecentLanguagesContentProvider
     */
    @Provides
    @Named("recent_languages")
    fun provideRecentLanguagesContentProviderClient(context: Context): ContentProviderClient {
        return context.contentResolver
            .acquireContentProviderClient(BuildConfig.RECENT_LANGUAGE_AUTHORITY)!!
    }

    /**
     * Provides a Json store instance(JsonKvStore) which keeps
     * the provided Gson in it's instance
     * @param gson stored inside the store instance
     */
    @Provides
    @Named("default_preferences")
    open fun providesDefaultKvStore(context: Context, gson: Gson?): JsonKvStore {
        val storeName = context.packageName + "_preferences"
        return JsonKvStore(context, storeName, gson)
    }

    @Provides
    fun providesUploadController(
        sessionManager: SessionManager?,
        @Named("default_preferences") kvStore: JsonKvStore?,
        context: Context?, contributionDao: ContributionDao?
    ): UploadController {
        return UploadController(sessionManager, context, kvStore)
    }

    @Provides
    @Singleton
    open fun provideLocationServiceManager(context: Context?): LocationServiceManager {
        return LocationServiceManager(context)
    }

    @Provides
    @Singleton
    open fun provideDBOpenHelper(context: Context?): DBOpenHelper {
        return DBOpenHelper(context)
    }

    @Provides
    @Singleton
    @Named("thumbnail-cache")
    open fun provideLruCache(): LruCache<String?, String?> {
        return LruCache(1024)
    }

    @Provides
    @Singleton
    fun provideWikidataEditListener(): WikidataEditListener {
        return WikidataEditListenerImpl()
    }

    /**
     * Provides app flavour. Can be used to alter flows in the app
     * @return
     */
    @Named("isBeta")
    @Provides
    @Singleton
    fun provideIsBetaVariant(): Boolean {
        return isBetaFlavour
    }

    /**
     * Provide JavaRx IO scheduler which manages IO operations
     * across various Threads
     */
    @Named(IO_THREAD)
    @Provides
    fun providesIoThread(): Scheduler {
        return Schedulers.io()
    }

    @Named(MAIN_THREAD)
    @Provides
    fun providesMainThread(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    @Named("username")
    @Provides
    fun provideLoggedInUsername(sessionManager: SessionManager): String {
        return Objects.toString(sessionManager.userName, "")
    }

    @Provides
    @Singleton
    fun provideAppDataBase(): AppDatabase {
        appDatabase =
            databaseBuilder(applicationContext, AppDatabase::class.java, "commons_room.db")
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
        return appDatabase!!
    }

    @Provides
    fun providesContributionsDao(appDatabase: AppDatabase): ContributionDao {
        return appDatabase.contributionDao()
    }

    /**
     * Get the reference of DepictsDao class.
     */
    @Provides
    fun providesDepictDao(appDatabase: AppDatabase): DepictsDao {
        return appDatabase.DepictsDao()
    }

    /**
     * Get the reference of UploadedStatus class.
     */
    @Provides
    fun providesUploadedStatusDao(appDatabase: AppDatabase): UploadedStatusDao {
        return appDatabase.UploadedStatusDao()
    }

    /**
     * Get the reference of NotForUploadStatus class.
     */
    @Provides
    fun providesNotForUploadStatusDao(appDatabase: AppDatabase): NotForUploadStatusDao {
        return appDatabase.NotForUploadStatusDao()
    }

    /**
     * Get the reference of ReviewDao class
     */
    @Provides
    fun providesReviewDao(appDatabase: AppDatabase): ReviewDao {
        return appDatabase.ReviewDao()
    }

    @Provides
    fun providesContentResolver(context: Context): ContentResolver {
        return context.contentResolver
    }

    companion object {
        const val IO_THREAD: String = "io_thread"
        const val MAIN_THREAD: String = "main_thread"
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE contribution "
                            + " ADD COLUMN hasInvalidLocation INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}
