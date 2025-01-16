package fr.free.nrw.commons.bookmarks.models

class Bookmark(
    mediaName: String?,
    mediaCreator: String?
) {
    /**
     * Gets the media name
     * @return the media name
     */
    val mediaName: String = mediaName ?: ""

    /**
     * Gets media creator
     * @return creator name
     */
    val mediaCreator: String = mediaCreator ?: ""
}
