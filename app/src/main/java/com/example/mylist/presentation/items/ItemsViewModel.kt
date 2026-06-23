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
import com.example.mylist.presentation.common.ListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
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

    private val retryTrigger = MutableStateFlow(0)

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    val uiState: StateFlow<ListUiState<List<ListItem>>> = retryTrigger.flatMapLatest {
        combine(
            getItemsUseCase(categoryId),
            _searchQuery,
            _isAscending
        ) { items, query, ascending ->
            val filtered = if (query.isBlank()) {
                items
            } else {
                items.filter { it.name.contains(query, ignoreCase = true) }
            }
            val sorted = if (ascending) {
                filtered.sortedBy { it.name.lowercase() }
            } else {
                filtered.sortedByDescending { it.name.lowercase() }
            }
            ListUiState.Success(sorted) as ListUiState<List<ListItem>>
        }.catch { e ->
            emit(ListUiState.Error(e.message ?: "Не удалось загрузить элементы"))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListUiState.Loading)

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun toggleSortOrder() {
        _isAscending.value = !_isAscending.value
    }

    fun retry() {
        retryTrigger.value++
    }

    fun addItem(name: String, description: String?, status: ItemStatus) {
        viewModelScope.launch {
            runCatching {
                addItemUseCase(
                    ListItem(categoryId = categoryId, name = name, description = description, status = status)
                )
            }.onFailure {
                _snackbarMessage.emit(it.message ?: "Не удалось добавить элемент")
            }
        }
    }

    fun updateItem(item: ListItem) {
        viewModelScope.launch {
            runCatching {
                updateItemUseCase(item)
            }.onFailure {
                _snackbarMessage.emit(it.message ?: "Не удалось обновить элемент")
            }
        }
    }

    fun deleteItem(item: ListItem) {
        viewModelScope.launch {
            runCatching {
                deleteItemUseCase(item)
            }.onFailure {
                _snackbarMessage.emit(it.message ?: "Не удалось удалить элемент")
            }
        }
    }

    fun updateStatus(itemId: Long, status: ItemStatus) {
        viewModelScope.launch {
            runCatching {
                updateItemStatusUseCase(itemId, status)
            }.onFailure {
                _snackbarMessage.emit(it.message ?: "Не удалось изменить статус")
            }
        }
    }
}
