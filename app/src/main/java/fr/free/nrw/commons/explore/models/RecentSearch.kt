package fr.free.nrw.commons.explore.models

import java.util.Date

/**
 * Represents a recently searched query
 * Example - query = "butterfly"
 */
class RecentSearch(
    /**
     * Gets query name
     * @return query name
     */
    val query: String,
    var lastSearched: Date,
)
