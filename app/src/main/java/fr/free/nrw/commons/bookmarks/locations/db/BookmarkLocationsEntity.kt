package fr.free.nrw.commons.bookmarks.locations.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarksLocations")
data class BookmarkLocationsEntity(
    @PrimaryKey @ColumnInfo(name = "location_name") val name: String,
    @ColumnInfo(name = "location_language") val language: String?,
    @ColumnInfo(name = "location_description") val description: String?,
    @ColumnInfo(name = "location_lat") val locationLat: Double?,
    @ColumnInfo(name = "location_long") val locationLong: Double?,
    @ColumnInfo(name = "location_category") val category: String?,
    @ColumnInfo(name = "location_label_text") val labelText: String?,
    @ColumnInfo(name = "location_label_icon") val labelIcon: Int?,
    @ColumnInfo(name = "location_image_url") val imageUrl: String?,
    @ColumnInfo(name = "location_wikipedia_link") val wikipediaLink: String?,
    @ColumnInfo(name = "location_wikidata_link") val wikidataLink: String?,
    @ColumnInfo(name = "location_commons_link") val commonsLink: String?,
    @ColumnInfo(name = "location_pic") val pic: String?,
    @ColumnInfo(name = "location_exists") val exists: String?,
)
