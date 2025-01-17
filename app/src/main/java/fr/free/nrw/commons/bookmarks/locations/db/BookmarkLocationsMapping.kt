package fr.free.nrw.commons.bookmarks.locations.db

import fr.free.nrw.commons.location.LatLng
import fr.free.nrw.commons.nearby.Label
import fr.free.nrw.commons.nearby.Place
import fr.free.nrw.commons.nearby.Sitelinks

fun BookmarkLocationsEntity.toDomain(): Place {
    val siteLinks = Sitelinks.Builder()
        .setWikipediaLink(wikipediaLink)
        .setWikidataLink(wikidataLink)
        .setCommonsLink(commonsLink)
        .build()

    val location = if (locationLat != null && locationLong != null) {
        LatLng(locationLat, locationLong, 1.0f)
    } else null

    return Place(
        /* language = */ language,
        /* name = */ name,
        /* label = */ Label.fromText(labelText),
        /* longDescription = */ description,
        /* location = */ location,
        /* category = */ category,
        /* siteLinks = */ siteLinks,
        /* pic = */ pic,
        /* exists = */ exists.toBoolean()
    )
}

fun Place.toEntity(): BookmarkLocationsEntity = BookmarkLocationsEntity(
    name = name,
    language = language,
    description = longDescription,
    locationLat = location.latitude,
    locationLong = location.longitude,
    category = category,
    labelText = label.text,
    labelIcon = label.icon,
    imageUrl = null,
    wikipediaLink = siteLinks?.wikipediaLink?.toString(),
    wikidataLink = siteLinks?.wikidataLink?.toString(),
    commonsLink = siteLinks?.commonsLink?.toString(),
    pic = pic,
    exists = exists?.toString()
)
