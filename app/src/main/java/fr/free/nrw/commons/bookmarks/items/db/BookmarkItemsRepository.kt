package fr.free.nrw.commons.bookmarks.items.db

import fr.free.nrw.commons.upload.structure.depictions.DepictedItem
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class BookmarkItemsRepository @Inject constructor(
    private val dao: BookmarkItemsDao
) {
    fun getAllBookmarksItems(): List<DepictedItem> = runBlocking {
        dao.allBookmarksItems().map { it.toDomain() }
    }

    fun updateBookmarkItem(depictedItem: DepictedItem) = runBlocking {
        dao.save(depictedItem.toEntity())
    }

    suspend fun addBookmarkItem(depictedItem: DepictedItem) =
        dao.save(depictedItem.toEntity())

    suspend fun deleteBookmarkItem(depictedItem: DepictedItem) =
        dao.delete(depictedItem.id)

    fun findBookmarkItem(depictedItemID: String?) = runBlocking {
        depictedItemID?.let { dao.find(it) } != null
    }
}