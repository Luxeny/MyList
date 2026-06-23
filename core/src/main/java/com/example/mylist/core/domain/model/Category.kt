package com.example.mylist.core.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val description: String?,
    val color: Int,
    val itemCount: Int = 0
)