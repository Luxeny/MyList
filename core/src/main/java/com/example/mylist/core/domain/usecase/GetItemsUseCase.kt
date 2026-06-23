package com.example.mylist.core.domain.usecase

import com.example.mylist.core.domain.model.ListItem
import com.example.mylist.core.domain.repository.MyListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetItemsUseCase @Inject constructor(
    private val repository: MyListRepository
) {
    operator fun invoke(categoryId: Long): Flow<List<ListItem>> = repository.getItemsForCategory(categoryId)
}