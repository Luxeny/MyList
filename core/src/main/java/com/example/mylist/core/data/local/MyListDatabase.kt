package com.example.mylist.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mylist.core.data.local.dao.CategoryDao
import com.example.mylist.core.data.local.dao.ItemDao
import com.example.mylist.core.data.local.entity.CategoryEntity
import com.example.mylist.core.data.local.entity.ItemEntity

@Database(
    entities = [CategoryEntity::class, ItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MyListDatabase : RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val itemDao: ItemDao
}