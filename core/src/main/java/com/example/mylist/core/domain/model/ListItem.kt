package com.example.mylist.core.domain.model

data class ListItem(
    val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val description: String?,
    val status: ItemStatus
)