package com.example.mylist.core.data.repository

import com.example.mylist.core.data.local.dao.CategoryDao
import com.example.mylist.core.data.local.dao.ItemDao
import com.example.mylist.core.data.mapper.toDomain
import com.example.mylist.core.data.mapper.toEntity
import com.example.mylist.core.domain.model.Category
import com.example.mylist.core.domain.model.ItemStatus
import com.example.mylist.core.domain.model.ListItem
import com.example.mylist.core.domain.repository.MyListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyListRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val itemDao: ItemDao
) : MyListRepository {

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategoriesWithCount().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    override fun getItemsForCategory(categoryId: Long): Flow<List<ListItem>> {
        return itemDao.getItemsForCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertItem(item: ListItem): Long {
        return itemDao.insertItem(item.toEntity())
    }

    override suspend fun updateItem(item: ListItem) {
        itemDao.updateItem(item.toEntity())
    }

    override suspend fun deleteItem(item: ListItem) {
        itemDao.deleteItem(item.toEntity())
    }

    override suspend fun updateItemStatus(itemId: Long, status: ItemStatus) {
        itemDao.updateItemStatus(itemId, status)
    }
}