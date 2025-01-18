package fr.free.nrw.commons.category.db

import fr.free.nrw.commons.category.Category
import fr.free.nrw.commons.category.CategoryItem
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {
    suspend fun save(category: Category) =
        dao.save(category.toEntity())

    suspend fun find(name: String): Category? =
        dao.find(name)?.toDomain()

    fun recentCategories(limit: Int): List<CategoryItem> = runBlocking {
        dao.recent(limit).map { it.toDomainItem() }
    }
}
