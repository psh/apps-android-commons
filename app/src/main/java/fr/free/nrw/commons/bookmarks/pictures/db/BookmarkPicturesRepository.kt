package fr.free.nrw.commons.bookmarks.pictures.db

import fr.free.nrw.commons.bookmarks.models.Bookmark
import javax.inject.Inject

class BookmarkPicturesRepository @Inject constructor(
    private val dao: BookmarkPicturesDao
) {
    fun findBookmark(bookmark: Bookmark): Boolean =
        dao.find(bookmark.mediaName) != null

    fun deleteBookmark(bookmark: Bookmark) =
        dao.delete(bookmark.mediaName)

    fun addBookmark(bookmark: Bookmark) =
        updateBookmark(bookmark)

    fun updateBookmark(bookmark: Bookmark) =
        dao.save(bookmark.toEntity())

    fun getAllBookmarks(): List<Bookmark> =
        dao.allBookmarks().map { it.toDomain() }
}
