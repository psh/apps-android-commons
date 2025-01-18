package fr.free.nrw.commons.bookmarks.locations.db

import fr.free.nrw.commons.nearby.Place
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class BookmarkLocationsRepository @Inject constructor(
    private val dao: BookmarkLocationsDao
) {
    fun getAllBookmarksLocations(): List<Place> = runBlocking {
        dao.allBookmarkLocations().map { it.toDomain() }
    }

    suspend fun updateBookmarkLocation(place: Place) =
        dao.save(place.toEntity())

    suspend fun addBookmarkLocation(place: Place) =
        dao.save(place.toEntity())

    suspend fun deleteBookmarkLocation(place: Place) =
        dao.delete(place.name)

    fun findBookmarkLocation(place: Place) : Boolean = runBlocking {
        dao.find(place.name) != null
    }
}