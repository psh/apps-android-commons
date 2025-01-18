package fr.free.nrw.commons.customselector.database

import fr.free.nrw.commons.customselector.model.UploadedStatus
import javax.inject.Inject

class UploadedStatusRepository @Inject constructor(
    private val dao: UploadedStatusDao
) {
    suspend fun insert(uploadedStatus: UploadedStatus) =
        dao.insert(uploadedStatus.toEntity())

    suspend fun update(uploadedStatus: UploadedStatus) =
        dao.update(uploadedStatus.toEntity())

    suspend fun delete(uploadedStatus: UploadedStatus) =
        dao.delete(uploadedStatus.toEntity())

    suspend fun getFromImageSHA1(imageSHA1: String): UploadedStatus? =
        dao.getFromImageSHA1(imageSHA1)?.toDomain()

    suspend fun getFromModifiedImageSHA1(modifiedImageSHA1: String): UploadedStatus? =
        dao.getFromModifiedImageSHA1(modifiedImageSHA1)?.toDomain()

    suspend fun findByImageSHA1(imageSHA1: String, imageResult: Boolean): Int =
        dao.findByImageSHA1(imageSHA1, imageResult)

    suspend fun findByModifiedImageSHA1(modifiedImageSHA1: String, modifiedImageResult: Boolean): Int =
        dao.findByModifiedImageSHA1(modifiedImageSHA1, modifiedImageResult)
}