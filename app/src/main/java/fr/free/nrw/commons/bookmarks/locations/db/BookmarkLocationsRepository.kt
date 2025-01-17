package fr.free.nrw.commons.bookmarks.locations.db

import fr.free.nrw.commons.nearby.Place
import javax.inject.Inject

class BookmarkLocationsRepository @Inject constructor(
    private val dao: BookmarkLocationsDaoTNG
) {
    fun getAllBookmarksLocations(): List<Place> =
        dao.allBookmarkLocations().map { it.toDomain() }

    fun updateBookmarkLocation(place: Place) =
        dao.save(place.toEntity())

    fun addBookmarkLocation(place: Place) =
        dao.save(place.toEntity())

    fun deleteBookmarkLocation(place: Place) =
        dao.delete(place.name)

    fun findBookmarkLocation(place: Place) : Boolean =
        dao.find(place.name) != null
}