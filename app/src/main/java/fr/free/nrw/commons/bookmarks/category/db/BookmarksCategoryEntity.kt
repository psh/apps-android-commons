package fr.free.nrw.commons.bookmarks.category.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing bookmarked category in DB
 *
 * @property categoryName
 * @constructor Create empty Bookmarks category modal
 */
@Entity(tableName = "bookmarks_categories")
data class BookmarksCategoryEntity(
    @PrimaryKey val categoryName: String
)
