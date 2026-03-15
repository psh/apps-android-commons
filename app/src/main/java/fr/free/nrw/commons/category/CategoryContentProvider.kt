package fr.free.nrw.commons.category


import android.content.ContentValues
import android.content.UriMatcher
import android.content.UriMatcher.NO_MATCH
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import androidx.core.net.toUri
import fr.free.nrw.commons.BuildConfig
import fr.free.nrw.commons.category.CategoryTable.ALL_FIELDS
import fr.free.nrw.commons.category.CategoryTable.COLUMN_ID
import fr.free.nrw.commons.category.CategoryTable.TABLE_NAME
import fr.free.nrw.commons.di.CommonsDaggerContentProvider

class CategoryContentProvider : CommonsDaggerContentProvider() {

    private val uriMatcher = UriMatcher(NO_MATCH).apply {
        addURI(BuildConfig.CATEGORY_AUTHORITY, BASE_PATH, CATEGORIES)
        addURI(BuildConfig.CATEGORY_AUTHORITY, "${BASE_PATH}/#", CATEGORIES_ID)
    }

    @SuppressWarnings("ConstantConditions")
    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor {
        val uriType = uriMatcher.match(uri)

        return when (uriType) {
            CATEGORIES -> requireDb().query(
                SupportSQLiteQueryBuilder.builder(TABLE_NAME)
                    .columns(projection)
                    .selection(selection, selectionArgs)
                    .orderBy(sortOrder)
                    .create()
            )

            CATEGORIES_ID -> requireDb().query(
                SupportSQLiteQueryBuilder.builder(TABLE_NAME)
                    .columns(ALL_FIELDS)
                    .selection("_id = ?", arrayOf(uri.lastPathSegment))
                    .orderBy(sortOrder)
                    .create()
            )

            else -> throw IllegalArgumentException("Unknown URI $uri")
        }.apply {
            setNotificationUri(context?.contentResolver, uri)
        }
    }

    override fun getType(uri: Uri): String? = null

    @SuppressWarnings("ConstantConditions")
    override fun insert(uri: Uri, contentValues: ContentValues?): Uri {
        val uriType = uriMatcher.match(uri)
        val id: Long
        when (uriType) {
            CATEGORIES -> {
                id = requireDb().insert(TABLE_NAME, CONFLICT_REPLACE, contentValues!!)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return "${BASE_URI}/$id".toUri()
    }

    @SuppressWarnings("ConstantConditions")
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    @SuppressWarnings("ConstantConditions")
    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        val uriType = uriMatcher.match(uri)
        val sqlDB = requireDb()
        sqlDB.beginTransaction()
        when (uriType) {
            CATEGORIES -> {
                for (value in values) {
                    sqlDB.insert(TABLE_NAME, CONFLICT_REPLACE, value)
                }
                sqlDB.setTransactionSuccessful()
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        sqlDB.endTransaction()
        context?.contentResolver?.notifyChange(uri, null)
        return values.size
    }

    @SuppressWarnings("ConstantConditions")
    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        val uriType = uriMatcher.match(uri)
        val rowsUpdated: Int
        when (uriType) {
            CATEGORIES_ID -> {
                if (TextUtils.isEmpty(selection)) {
                    val id = uri.lastPathSegment?.toInt()
                        ?: throw IllegalArgumentException("Invalid ID")
                    rowsUpdated = requireDb().update(
                        TABLE_NAME,
                        CONFLICT_REPLACE, contentValues!!,
                        "$COLUMN_ID = ?",
                        arrayOf(id.toString())
                    )
                } else {
                    throw IllegalArgumentException(
                        "Parameter `selection` should be empty when updating an ID")
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri with type $uriType")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return rowsUpdated
    }

    companion object {
        fun uriForId(id: Int): Uri = Uri.parse("${BASE_URI}/$id")

        // For URI matcher
        private const val CATEGORIES = 1
        private const val CATEGORIES_ID = 2
        private const val BASE_PATH = "categories"
        val  BASE_URI: Uri = "content://${BuildConfig.CATEGORY_AUTHORITY}/${BASE_PATH}".toUri()
    }
}
