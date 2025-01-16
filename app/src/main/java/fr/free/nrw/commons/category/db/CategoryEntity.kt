package fr.free.nrw.commons.category.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val name: String?,
    val description: String?,
    val thumbnail: String?,
    @ColumnInfo(name = "last_used")
    val lastUsed: Long?,
    @ColumnInfo(name = "times_used")
    val timesUsed: Int
)