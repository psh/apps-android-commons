package fr.free.nrw.commons.recentlanguages.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RecentLanguagesDao {
    @Upsert
    fun save(language: RecentLanguagesEntity)

    @Query("select * from recent_languages where language_code=:code")
    fun find(code: String): RecentLanguagesEntity?

    @Query("select * from recent_languages")
    fun recent(): List<RecentLanguagesEntity>

    @Query("delete from recent_languages where language_code=:code")
    fun delete(code: String)
}