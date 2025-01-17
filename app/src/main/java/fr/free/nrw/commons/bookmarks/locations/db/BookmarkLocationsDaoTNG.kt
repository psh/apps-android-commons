package fr.free.nrw.commons.bookmarks.locations.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BookmarkLocationsDaoTNG {
    @Query("select * from bookmarksLocations")
    fun allBookmarkLocations(): List<BookmarkLocationsEntity>

    @Query("select * from bookmarksLocations where location_name=:name")
    fun find(name: String) : BookmarkLocationsEntity?

    @Query("delete from bookmarksLocations")
    fun deleteAll()

    @Query("delete from bookmarksLocations where location_name=:name")
    fun delete(name: String)

    @Upsert
    fun save(location: BookmarkLocationsEntity)
}