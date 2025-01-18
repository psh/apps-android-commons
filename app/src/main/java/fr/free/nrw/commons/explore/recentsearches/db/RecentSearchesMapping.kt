package fr.free.nrw.commons.explore.recentsearches.db

import fr.free.nrw.commons.explore.models.RecentSearch
import java.util.Date

fun RecentSearchesEntity.toDomain(): RecentSearch = RecentSearch(
    query = name, lastSearched = Date(lastUsed)
)

fun RecentSearch.toEntity(): RecentSearchesEntity = RecentSearchesEntity(
    id = 0, name = query, lastUsed = lastSearched.time
)
