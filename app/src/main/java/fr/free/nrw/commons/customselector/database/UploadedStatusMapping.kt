package fr.free.nrw.commons.customselector.database

import fr.free.nrw.commons.customselector.model.UploadedStatus

fun UploadedStatusEntity.toDomain() : UploadedStatus = UploadedStatus(
    imageSHA1 = imageSHA1,
    modifiedImageSHA1 = modifiedImageSHA1,
    imageResult = imageResult,
    modifiedImageResult = modifiedImageResult,
    lastUpdated = lastUpdated
)

fun UploadedStatus.toEntity(): UploadedStatusEntity = UploadedStatusEntity(
    imageSHA1 = imageSHA1,
    modifiedImageSHA1 = modifiedImageSHA1,
    imageResult = imageResult,
    modifiedImageResult = modifiedImageResult,
    lastUpdated = lastUpdated
)
