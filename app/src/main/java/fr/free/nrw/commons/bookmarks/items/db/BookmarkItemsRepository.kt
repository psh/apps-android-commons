package fr.free.nrw.commons.bookmarks.items.db

import fr.free.nrw.commons.upload.structure.depictions.DepictedItem
import javax.inject.Inject

class BookmarkItemsRepository @Inject constructor(
    private val dao: BookmarkItemsDaoTNG
) {
    fun getAllBookmarksItems(): List<DepictedItem> =
        dao.allBookmarksItems().map { it.toDomain() }

    fun updateBookmarkItem(depictedItem: DepictedItem) =
        dao.save(depictedItem.toEntity())

    fun addBookmarkItem(depictedItem: DepictedItem) =
        dao.save(depictedItem.toEntity())

    fun deleteBookmarkItem(depictedItem: DepictedItem) =
        dao.delete(depictedItem.id)

    fun findBookmarkItem(depictedItemID: String?) =
        depictedItemID?.let { dao.find(it) } != null
}