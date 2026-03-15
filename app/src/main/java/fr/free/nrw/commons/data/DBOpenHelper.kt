package fr.free.nrw.commons.data

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import fr.free.nrw.commons.bookmarks.items.BookmarkItemsTable
import fr.free.nrw.commons.bookmarks.pictures.BookmarksTable
import fr.free.nrw.commons.category.CategoryTable
import fr.free.nrw.commons.explore.recentsearches.RecentSearchesTable
import fr.free.nrw.commons.recentlanguages.RecentLanguagesTable

class DBOpenHelper(val helper: SupportSQLiteOpenHelper) {

    companion object {
        private const val DATABASE_NAME = "commons.db"
        private const val DATABASE_VERSION = 22
        const val CONTRIBUTIONS_TABLE = "contributions"
        const val BOOKMARKS_LOCATIONS = "bookmarksLocations"
        private const val DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS %s"

        fun createHelper(context: Context): SupportSQLiteOpenHelper {
            return FrameworkSQLiteOpenHelperFactory().create(
                SupportSQLiteOpenHelper.Configuration.builder(context)
                    .name(DATABASE_NAME)
                    .callback(CommonsDatabaseCallback(DATABASE_VERSION))
                    .build()
            )
        }

        /**
         * Delete table in the given db
         * @param tableName
         */
        fun SupportSQLiteDatabase.deleteTable(tableName: String) {
            try {
                execSQL(String.format(DROP_TABLE_STATEMENT, tableName))
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
        }
    }


    val readableDatabase: SupportSQLiteDatabase get() = helper.readableDatabase
    val writableDatabase: SupportSQLiteDatabase get() = helper.writableDatabase

    class CommonsDatabaseCallback(version: Int) : SupportSQLiteOpenHelper.Callback(version) {
        override fun onCreate(db: SupportSQLiteDatabase) {
            CategoryTable.onCreate(db)
            BookmarksTable.onCreate(db)
            BookmarkItemsTable.onCreate(db)
            RecentSearchesTable.onCreate(db)
            RecentLanguagesTable.onCreate(db)
        }

        override fun onUpgrade(db: SupportSQLiteDatabase, from: Int, to: Int) {
            CategoryTable.onUpdate(db, from, to)
            BookmarksTable.onUpdate(db, from, to)
            BookmarkItemsTable.onUpdate(db, from, to)
            RecentSearchesTable.onUpdate(db, from, to)
            RecentLanguagesTable.onUpdate(db, from, to)
            db.deleteTable(CONTRIBUTIONS_TABLE)
            db.deleteTable(BOOKMARKS_LOCATIONS)
        }
    }
}
