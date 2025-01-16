package fr.free.nrw.commons.bookmarks.pictures.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BookmarkPicturesDao {
    @Upsert
    fun save(bookmarkPicturesEntity: BookmarkPicturesEntity)

    @Query("select * from bookmarks")
    fun allBookmarks(): List<BookmarkPicturesEntity>

    @Query("select * from bookmarks where media_name=:name")
    fun find(name: String?): BookmarkPicturesEntity?

    @Query("delete from bookmarks where media_name=:name")
    fun delete(name: String?)

    @Query("delete from bookmarks")
    fun deleteAll()
}