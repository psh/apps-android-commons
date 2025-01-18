package fr.free.nrw.commons.explore.recentsearches.db

import fr.free.nrw.commons.explore.models.RecentSearch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class RecentSearchesRepository @Inject constructor(
    private val dao: RecentSearchesDao
) {
    fun save(recentSearch: RecentSearch) = runBlocking {
        dao.save(recentSearch.toEntity())
    }

    fun deleteAll() = runBlocking {
        dao.deleteAll()
    }

    fun delete(recentSearch: RecentSearch) = runBlocking {
        dao.delete(recentSearch.query)
    }

    fun find(name: String): RecentSearch? = runBlocking {
        dao.find(name)?.toDomain()
    }

    fun recentSearches(limit: Int): List<String> = runBlocking {
        dao.recentSearches(limit).map { it.toString() }
    }
}