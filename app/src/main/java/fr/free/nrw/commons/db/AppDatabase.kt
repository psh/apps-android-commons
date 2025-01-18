package fr.free.nrw.commons.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.free.nrw.commons.bookmarks.category.BookmarkCategoriesDao
import fr.free.nrw.commons.bookmarks.category.BookmarksCategoryModal
import fr.free.nrw.commons.bookmarks.items.db.BookmarkItemsDao
import fr.free.nrw.commons.bookmarks.items.db.BookmarkItemsEntity
import fr.free.nrw.commons.bookmarks.locations.db.BookmarkLocationsDao
import fr.free.nrw.commons.bookmarks.locations.db.BookmarkLocationsEntity
import fr.free.nrw.commons.bookmarks.pictures.db.BookmarkPicturesDao
import fr.free.nrw.commons.bookmarks.pictures.db.BookmarkPicturesEntity
import fr.free.nrw.commons.category.db.CategoryDao
import fr.free.nrw.commons.category.db.CategoryEntity
import fr.free.nrw.commons.contributions.Contribution
import fr.free.nrw.commons.contributions.ContributionDao
import fr.free.nrw.commons.customselector.database.NotForUploadStatus
import fr.free.nrw.commons.customselector.database.NotForUploadStatusDao
import fr.free.nrw.commons.customselector.database.UploadedStatusEntity
import fr.free.nrw.commons.customselector.database.UploadedStatusDao
import fr.free.nrw.commons.explore.recentsearches.db.RecentSearchesDao
import fr.free.nrw.commons.explore.recentsearches.db.RecentSearchesEntity
import fr.free.nrw.commons.nearby.Place
import fr.free.nrw.commons.nearby.PlaceDao
import fr.free.nrw.commons.recentlanguages.db.RecentLanguagesDao
import fr.free.nrw.commons.recentlanguages.db.RecentLanguagesEntity
import fr.free.nrw.commons.review.ReviewDao
import fr.free.nrw.commons.review.ReviewEntity
import fr.free.nrw.commons.upload.depicts.Depicts
import fr.free.nrw.commons.upload.depicts.DepictsDao

/**
 * The database for accessing the respective DAOs
 */
@Database(
    entities = [
        BookmarkItemsEntity::class,
        BookmarkLocationsEntity::class,
        BookmarkPicturesEntity::class,
        BookmarksCategoryModal::class,
        CategoryEntity::class,
        Contribution::class,
        Depicts::class,
        NotForUploadStatus::class,
        Place::class,
        RecentLanguagesEntity::class,
        RecentSearchesEntity::class,
        ReviewEntity::class,
        UploadedStatusEntity::class,
    ],
    version = 19,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkCategoriesDao(): BookmarkCategoriesDao
    abstract fun bookmarkItemsDao(): BookmarkItemsDao
    abstract fun bookmarkLocationsDao(): BookmarkLocationsDao
    abstract fun bookmarkPicturesDao(): BookmarkPicturesDao
    abstract fun categoryDao(): CategoryDao
    abstract fun contributionDao(): ContributionDao
    abstract fun depictsDao(): DepictsDao
    abstract fun notForUploadStatusDao(): NotForUploadStatusDao
    abstract fun placeDao(): PlaceDao
    abstract fun recentLanguagesDao(): RecentLanguagesDao
    abstract fun recentSearchesDao(): RecentSearchesDao
    abstract fun reviewDao(): ReviewDao
    abstract fun uploadedStatusDao(): UploadedStatusDao
}
