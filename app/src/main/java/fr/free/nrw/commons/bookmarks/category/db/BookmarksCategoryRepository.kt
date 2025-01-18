package fr.free.nrw.commons.bookmarks.category.db

import fr.free.nrw.commons.bookmarks.category.BookmarksCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarksCategoryRepository @Inject constructor(
    private val dao: BookmarkCategoriesDao
) {
    suspend fun insert(categoryName: String) =
        dao.insert(BookmarksCategoryEntity(categoryName))

    suspend fun delete(categoryName: String) =
        dao.delete(BookmarksCategoryEntity(categoryName))

    suspend fun doesExist(categoryName: String): Boolean =
        dao.doesExist(categoryName)

    fun getAllCategories(): Flow<List<BookmarksCategory>> =
        dao.getAllCategories().mapLatest {
            it.map { cat -> BookmarksCategory(cat.categoryName) }
        }
}