package fr.free.nrw.commons.bookmarks.pictures.db

import fr.free.nrw.commons.bookmarks.models.Bookmark

fun Bookmark.toEntity(): BookmarkPicturesEntity = BookmarkPicturesEntity(
    name = mediaName, creator = mediaCreator
)

fun BookmarkPicturesEntity.toDomain(): Bookmark = Bookmark(
    mediaName = name, mediaCreator = creator
)