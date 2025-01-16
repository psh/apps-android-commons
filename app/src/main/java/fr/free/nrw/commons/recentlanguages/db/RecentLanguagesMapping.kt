package fr.free.nrw.commons.recentlanguages.db

import fr.free.nrw.commons.recentlanguages.Language

fun Language.toEntity(): RecentLanguagesEntity = RecentLanguagesEntity(
    name = languageName, code = languageCode
)

fun RecentLanguagesEntity.toDomain(): Language = Language(
    languageCode = code, languageName = name
)
