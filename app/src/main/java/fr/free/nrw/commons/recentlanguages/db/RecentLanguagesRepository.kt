package fr.free.nrw.commons.recentlanguages.db

import fr.free.nrw.commons.recentlanguages.Language
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class RecentLanguagesRepository @Inject constructor(
    private val dao: RecentLanguagesDao
) {
    fun getRecentLanguages(): List<Language> = runBlocking {
        dao.recent().map { it.toDomain() }
    }

    fun addRecentLanguage(language: Language) = runBlocking {
        dao.save(language.toEntity())
    }

    fun deleteRecentLanguage(languageCode: String) = runBlocking {
        dao.delete(languageCode)
    }

    fun findRecentLanguage(languageCode: String?): Boolean = runBlocking {
        languageCode?.let { dao.find(it) != null } ?: false
    }
}
