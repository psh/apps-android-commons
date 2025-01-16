package fr.free.nrw.commons.bookmarks.pictures.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkPicturesEntity(
    @PrimaryKey
    @ColumnInfo(name = "media_name")
    val name: String,
    @ColumnInfo(name = "media_creator")
    val creator: String?,
)