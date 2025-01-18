package fr.free.nrw.commons.bookmarks.items.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarksItems")
data class BookmarkItemsEntity(
    @PrimaryKey @ColumnInfo(name = "item_id") val id: String,
    @ColumnInfo(name = "item_name") val name: String?,
    @ColumnInfo(name = "item_description") val description: String?,
    @ColumnInfo(name = "item_image_url") val imageUrl: String?,
    @ColumnInfo(name = "item_instance_of") val instanceOf: String,
    @ColumnInfo(name = "item_name_categories") val nameCategories: String,
    @ColumnInfo(name = "item_description_categories") val descriptionCategories: String,
    @ColumnInfo(name = "item_thumbnail_categories") val thumbnailCategories: String,
    @ColumnInfo(name = "item_is_selected") val isSelected: Boolean,
)