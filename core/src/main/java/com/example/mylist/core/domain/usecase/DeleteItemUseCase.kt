package com.example.mylist.core.domain.usecase

import com.example.mylist.core.domain.model.ListItem
import com.example.mylist.core.domain.repository.MyListRepository
import javax.inject.Inject

class DeleteItemUseCase @Inject constructor(
    private val repository: MyListRepository
) {
    suspend operator fun invoke(item: ListItem) = repository.deleteItem(item)
}