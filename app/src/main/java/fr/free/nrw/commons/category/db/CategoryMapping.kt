package fr.free.nrw.commons.category.db

import fr.free.nrw.commons.category.Category
import fr.free.nrw.commons.category.CategoryItem
import java.util.Date

fun CategoryEntity.toDomain(): Category = Category(
    contentUri = null,
    name = name,
    description = description,
    thumbnail = thumbnail,
    lastUsed = lastUsed?.let { Date(it) },
    timesUsed = timesUsed
)

fun CategoryEntity.toDomainItem(): CategoryItem = CategoryItem(
    name = name ?: "",
    description = description,
    thumbnail = thumbnail,
    isSelected = false
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    name = name,
    description = description,
    thumbnail = thumbnail,
    lastUsed = lastUsed?.time,
    timesUsed = timesUsed
)
