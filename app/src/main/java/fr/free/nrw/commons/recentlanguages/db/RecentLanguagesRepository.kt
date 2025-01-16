package fr.free.nrw.commons.recentlanguages.db

import fr.free.nrw.commons.recentlanguages.Language
import javax.inject.Inject

class RecentLanguagesRepository @Inject constructor(
    private val dao: RecentLanguagesDao
) {
    fun getRecentLanguages(): List<Language> =
        dao.recent().map { it.toDomain() }

    fun addRecentLanguage(language: Language) =
        dao.save(language.toEntity())

    fun deleteRecentLanguage(languageCode: String) =
        dao.delete(languageCode)

    fun findRecentLanguage(languageCode: String?): Boolean =
        languageCode?.let { dao.find(it) != null } ?: false
}
