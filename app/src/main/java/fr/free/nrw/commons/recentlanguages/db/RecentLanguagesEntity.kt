package fr.free.nrw.commons.recentlanguages.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_languages")
data class RecentLanguagesEntity(
    @PrimaryKey
    @ColumnInfo(name = "language_code")
    val name: String,
    @ColumnInfo(name = "language_name")
    val code: String
)