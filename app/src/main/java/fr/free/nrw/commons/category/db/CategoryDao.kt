package fr.free.nrw.commons.category.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CategoryDao {
    @Upsert
    suspend fun save(category: CategoryEntity)

    @Query("select * from categories where name=:name")
    suspend fun find(name: String): CategoryEntity?

    @Query("select * from categories order by last_used desc limit :limit")
    suspend fun recent(limit: Int): List<CategoryEntity>
}