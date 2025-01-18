package fr.free.nrw.commons.customselector.model

import java.util.Date

data class UploadedStatus(
    val imageSHA1: String,
    val modifiedImageSHA1: String,
    var imageResult: Boolean,
    var modifiedImageResult: Boolean,
    var lastUpdated: Date? = null,
)
