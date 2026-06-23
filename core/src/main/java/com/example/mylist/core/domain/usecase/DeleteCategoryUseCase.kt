package com.example.mylist.core.domain.usecase

import com.example.mylist.core.domain.model.Category
import com.example.mylist.core.domain.repository.MyListRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: MyListRepository
) {
    suspend operator fun invoke(category: Category) = repository.deleteCategory(category)
}