package fr.free.nrw.commons.explore.recentsearches.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RecentSearchesDao {
    @Upsert
    suspend fun save(searchesEntity: RecentSearchesEntity)

    @Query("delete from recent_searches")
    suspend fun deleteAll()

    @Query("delete from recent_searches where name=:name")
    suspend fun delete(name: String)

    @Query("select * from recent_searches where name=:name")
    suspend fun find(name: String): RecentSearchesEntity?

    @Query("select * from recent_searches order by last_used desc limit :limit")
    suspend fun recentSearches(limit: Int): List<RecentSearchesEntity>
}