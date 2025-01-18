package fr.free.nrw.commons.bookmarks.pictures.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BookmarkPicturesDao {
    @Upsert
    suspend fun save(bookmarkPicturesEntity: BookmarkPicturesEntity)

    @Query("select * from bookmarks")
    suspend fun allBookmarks(): List<BookmarkPicturesEntity>

    @Query("select * from bookmarks where media_name=:name")
    suspend fun find(name: String?): BookmarkPicturesEntity?

    @Query("delete from bookmarks where media_name=:name")
    suspend fun delete(name: String?)

    @Query("delete from bookmarks")
    suspend fun deleteAll()
}