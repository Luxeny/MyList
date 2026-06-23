package com.example.mylist.core.data.mapper

import com.example.mylist.core.data.local.entity.CategoryEntity
import com.example.mylist.core.data.local.entity.CategoryWithCountEntity
import com.example.mylist.core.data.local.entity.ItemEntity
import com.example.mylist.core.domain.model.Category
import com.example.mylist.core.domain.model.ListItem

fun CategoryEntity.toDomain(itemCount: Int = 0): Category {
    return Category(
        id = id,
        name = name,
        description = description,
        color = color,
        itemCount = itemCount
    )
}

fun CategoryWithCountEntity.toDomain(): Category {
    return category.toDomain(itemCount)
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        description = description,
        color = color
    )
}

fun ItemEntity.toDomain(): ListItem {
    return ListItem(
        id = id,
        categoryId = categoryId,
        name = name,
        description = description,
        status = status
    )
}

fun ListItem.toEntity(): ItemEntity {
    return ItemEntity(
        id = id,
        categoryId = categoryId,
        name = name,
        description = description,
        status = status
    )
}