package fr.free.nrw.commons.explore.recentsearches.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_searches")
data class RecentSearchesEntity(
    @PrimaryKey @ColumnInfo(name = "_id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "last_used") val lastUsed: Long,
)
