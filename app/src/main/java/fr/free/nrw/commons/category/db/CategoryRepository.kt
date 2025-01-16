package fr.free.nrw.commons.category.db

import fr.free.nrw.commons.category.Category
import fr.free.nrw.commons.category.CategoryItem
import javax.inject.Inject


class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {
    fun save(category: Category) =
        dao.save(category.toEntity())

    fun find(name: String): Category? =
        dao.find(name)?.toDomain()

    fun recentCategories(limit: Int): List<CategoryItem> =
        dao.recent(limit).map { it.toDomainItem() }
}
