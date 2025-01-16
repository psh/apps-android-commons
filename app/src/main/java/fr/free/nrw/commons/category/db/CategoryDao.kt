package fr.free.nrw.commons.category.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CategoryDao {
    @Upsert
    fun save(category: CategoryEntity)

    @Query("select * from categories where name=:name")
    fun find(name: String): CategoryEntity?

    @Query("select * from categories order by last_used desc limit :limit")
    fun recent(limit: Int): List<CategoryEntity>
}