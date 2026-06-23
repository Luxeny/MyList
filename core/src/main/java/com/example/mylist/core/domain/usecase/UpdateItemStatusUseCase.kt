package com.example.mylist.core.domain.usecase

import com.example.mylist.core.domain.model.ItemStatus
import com.example.mylist.core.domain.repository.MyListRepository
import javax.inject.Inject

class UpdateItemStatusUseCase @Inject constructor(
    private val repository: MyListRepository
) {
    suspend operator fun invoke(itemId: Long, status: ItemStatus) = repository.updateItemStatus(itemId, status)
}