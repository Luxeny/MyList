package com.example.mylist.core.di

import com.example.mylist.core.data.repository.MyListRepositoryImpl
import com.example.mylist.core.domain.repository.MyListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMyListRepository(
        myListRepositoryImpl: MyListRepositoryImpl
    ): MyListRepository
}