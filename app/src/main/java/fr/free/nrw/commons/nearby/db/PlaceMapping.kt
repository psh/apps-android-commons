package fr.free.nrw.commons.nearby.db

import fr.free.nrw.commons.nearby.Place

fun Place.toEntity(): PlaceEntity = PlaceEntity(
    entityID = entityID,
    language = language,
    name = name,
    label = label,
    longDescription = longDescription,
    category = category,
    pic = pic,
    exists = exists,
    distance = distance,
    siteLinks = siteLinks,
    isMonument = isMonument,
    thumb = thumb,
    location = location
)

fun PlaceEntity.toDomain(): Place = Place(
    language, name, label, longDescription, location, category, siteLinks, pic, exists, entityID
)