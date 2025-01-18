package fr.free.nrw.commons.bookmarks.locations.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BookmarkLocationsDao {
    @Query("select * from bookmarksLocations")
    suspend fun allBookmarkLocations(): List<BookmarkLocationsEntity>

    @Query("select * from bookmarksLocations where location_name=:name")
    suspend fun find(name: String) : BookmarkLocationsEntity?

    @Query("delete from bookmarksLocations")
    suspend fun deleteAll()

    @Query("delete from bookmarksLocations where location_name=:name")
    suspend fun delete(name: String)

    @Upsert
    suspend fun save(location: BookmarkLocationsEntity)
}