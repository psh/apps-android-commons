package fr.free.nrw.commons.bookmarks.items.db

import fr.free.nrw.commons.category.CategoryItem
import fr.free.nrw.commons.upload.structure.depictions.DepictedItem

fun DepictedItem.toEntity(): BookmarkItemsEntity = BookmarkItemsEntity(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl,
    instanceOf = instanceOfs.joinToString(","),
    nameCategories = commonsCategories.joinToString(",") { it.name },
    descriptionCategories = commonsCategories.joinToString(",") { it.description ?: "" },
    thumbnailCategories = commonsCategories.joinToString(",") { it.thumbnail ?: "" },
    isSelected = isSelected
)

fun BookmarkItemsEntity.toDomain(): DepictedItem {
    return DepictedItem(
        id = id,
        name = name ?: "",
        description = description,
        imageUrl = imageUrl,
        instanceOfs = instanceOf.split(","),
        commonsCategories = nameCategories.split(",")
            .zip(descriptionCategories.split(","))
            .zip(thumbnailCategories.split(",")) { (n, d), t ->
                CategoryItem(n, d, t, false)
            },
        isSelected = isSelected,
    )
}