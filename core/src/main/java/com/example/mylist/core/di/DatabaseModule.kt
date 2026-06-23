package com.example.mylist.core.di

import android.content.Context
import androidx.room.Room
import com.example.mylist.core.data.local.MyListDatabase
import com.example.mylist.core.data.local.dao.CategoryDao
import com.example.mylist.core.data.local.dao.ItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyListDatabase {
        return Room.databaseBuilder(
            context,
            MyListDatabase::class.java,
            "mylist_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(db: MyListDatabase): CategoryDao = db.categoryDao

    @Provides
    @Singleton
    fun provideItemDao(db: MyListDatabase): ItemDao = db.itemDao
}