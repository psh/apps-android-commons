package fr.free.nrw.commons.bookmarks.items.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BookmarkItemsDaoTNG {
    @Query("select * from bookmarksItems")
    fun allBookmarksItems(): List<BookmarkItemsEntity>

    @Upsert
    fun save(entity: BookmarkItemsEntity)

    @Query("delete from bookmarksItems")
    fun deleteAll()

    @Query("delete from bookmarksItems where item_id=:id")
    fun delete(id: String)

    @Query("select * from bookmarksItems where item_id=:id")
    fun find(id: String): BookmarkItemsEntity?
}