package fr.free.nrw.commons.bookmarks.items.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BookmarkItemsDao {
    @Query("select * from bookmarksItems")
    suspend fun allBookmarksItems(): List<BookmarkItemsEntity>

    @Upsert
    suspend fun save(entity: BookmarkItemsEntity)

    @Query("delete from bookmarksItems")
    suspend fun deleteAll()

    @Query("delete from bookmarksItems where item_id=:id")
    suspend fun delete(id: String)

    @Query("select * from bookmarksItems where item_id=:id")
    suspend fun find(id: String): BookmarkItemsEntity?
}