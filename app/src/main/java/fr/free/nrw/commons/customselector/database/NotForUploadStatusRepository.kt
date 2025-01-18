package fr.free.nrw.commons.customselector.database

import javax.inject.Inject

class NotForUploadStatusRepository @Inject constructor(
    private val dao: NotForUploadStatusDao
) {
    suspend fun insert(imageSHA1: String) =
        dao.insert(NotForUploadStatusEntity(imageSHA1))

    suspend fun delete(imageSHA1: String) =
        dao.delete(NotForUploadStatusEntity(imageSHA1))

    suspend fun find(imageSHA1: String): Int =
        dao.find(imageSHA1)
}
