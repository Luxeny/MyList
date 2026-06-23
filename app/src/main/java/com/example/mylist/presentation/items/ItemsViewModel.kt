package com.example.mylist.presentation.items

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mylist.core.domain.model.ItemStatus
import com.example.mylist.core.domain.model.ListItem
import com.example.mylist.core.domain.usecase.AddItemUseCase
import com.example.mylist.core.domain.usecase.DeleteItemUseCase
import com.example.mylist.core.domain.usecase.GetItemsUseCase
import com.example.mylist.core.domain.usecase.UpdateItemStatusUseCase
import com.example.mylist.core.domain.usecase.UpdateItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
    private val addItemUseCase: AddItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val updateItemStatusUseCase: UpdateItemStatusUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val categoryId: Long = checkNotNull(savedStateHandle["categoryId"])

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isAscending = MutableStateFlow(true)
    val isAscending: StateFlow<Boolean> = _isAscending

    val items: StateFlow<List<ListItem>> = combine(
        getItemsUseCase(categoryId),
        _searchQuery,
        _isAscending
    ) { items, query, ascending ->
        val filtered = if (query.isBlank()) {
            items
        } else {
            items.filter { it.name.contains(query, ignoreCase = true) }
        }
        if (ascending) {
            filtered.sortedBy { it.name.lowercase() }
        } else {
            filtered.sortedByDescending { it.name.lowercase() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun toggleSortOrder() {
        _isAscending.value = !_isAscending.value
    }

    fun addItem(name: String, description: String?, status: ItemStatus) {
        viewModelScope.launch {
            addItemUseCase(ListItem(categoryId = categoryId, name = name, description = description, status = status))
        }
    }

    fun updateItem(item: ListItem) {
        viewModelScope.launch {
            updateItemUseCase(item)
        }
    }

    fun deleteItem(item: ListItem) {
        viewModelScope.launch {
            deleteItemUseCase(item)
        }
    }

    fun updateStatus(itemId: Long, status: ItemStatus) {
        viewModelScope.launch {
            updateItemStatusUseCase(itemId, status)
        }
    }
}