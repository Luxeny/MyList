package com.example.mylist.core.domain.repository

import com.example.mylist.core.domain.model.Category
import com.example.mylist.core.domain.model.ItemStatus
import com.example.mylist.core.domain.model.ListItem
import kotlinx.coroutines.flow.Flow

interface MyListRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun getCategoryById(id: Long): Category?

    fun getItemsForCategory(categoryId: Long): Flow<List<ListItem>>
    suspend fun insertItem(item: ListItem): Long
    suspend fun updateItem(item: ListItem)
    suspend fun deleteItem(item: ListItem)
    suspend fun updateItemStatus(itemId: Long, status: ItemStatus)
}