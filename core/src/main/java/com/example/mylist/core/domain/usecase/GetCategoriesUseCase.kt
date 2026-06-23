package com.example.mylist.core.domain.usecase

import com.example.mylist.core.domain.model.Category
import com.example.mylist.core.domain.repository.MyListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: MyListRepository
) {
    operator fun invoke(): Flow<List<Category>> = repository.getCategories()
}