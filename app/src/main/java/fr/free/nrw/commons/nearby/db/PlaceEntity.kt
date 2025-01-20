package fr.free.nrw.commons.nearby.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.free.nrw.commons.location.LatLng
import fr.free.nrw.commons.nearby.Label
import fr.free.nrw.commons.nearby.Sitelinks

@Entity(tableName = "place")
data class PlaceEntity(
    @PrimaryKey var entityID: String,
    val language: String? = null,
    val name: String? = null,
    val label: Label? = null,
    val longDescription: String? = null,
    val category: String? = null,
    val pic: String? = null,
    val exists: Boolean? = null,
    val distance: String? = null,
    val siteLinks: Sitelinks? = null,
    val isMonument: Boolean = false,
    val thumb: String? = null,
    @Embedded var location: LatLng? = null,
)
