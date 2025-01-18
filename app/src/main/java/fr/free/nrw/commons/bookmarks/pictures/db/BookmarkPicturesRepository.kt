package fr.free.nrw.commons.bookmarks.pictures.db

import fr.free.nrw.commons.bookmarks.models.Bookmark
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class BookmarkPicturesRepository @Inject constructor(
    private val dao: BookmarkPicturesDao
) {
    fun findBookmark(bookmark: Bookmark): Boolean = runBlocking {
        dao.find(bookmark.mediaName) != null
    }

    suspend fun deleteBookmark(bookmark: Bookmark) =
        dao.delete(bookmark.mediaName)

    fun addBookmark(bookmark: Bookmark) =
        updateBookmark(bookmark)

    fun updateBookmark(bookmark: Bookmark) = runBlocking {
        dao.save(bookmark.toEntity())
    }

    fun getAllBookmarks(): List<Bookmark> = runBlocking {
        dao.allBookmarks().map { it.toDomain() }
    }
}
