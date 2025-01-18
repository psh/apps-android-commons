package fr.free.nrw.commons.recentlanguages.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RecentLanguagesDao {
    @Upsert
    suspend fun save(language: RecentLanguagesEntity)

    @Query("select * from recent_languages where language_code=:code")
    suspend fun find(code: String): RecentLanguagesEntity?

    @Query("select * from recent_languages")
    suspend fun recent(): List<RecentLanguagesEntity>

    @Query("delete from recent_languages where language_code=:code")
    suspend fun delete(code: String)
}