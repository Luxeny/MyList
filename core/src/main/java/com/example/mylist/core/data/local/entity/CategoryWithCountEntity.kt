package com.example.mylist.core.data.local.entity

import androidx.room.Embedded

data class CategoryWithCountEntity(
    @Embedded val category: CategoryEntity,
    val itemCount: Int
)