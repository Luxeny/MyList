package com.example.mylist.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mylist.analytics.AppAnalytics
import com.example.mylist.core.domain.model.Category
import com.example.mylist.core.domain.usecase.AddCategoryUseCase
import com.example.mylist.core.domain.usecase.DeleteCategoryUseCase
import com.example.mylist.core.domain.usecase.GetCategoriesUseCase
import com.example.mylist.core.domain.usecase.UpdateCategoryUseCase
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
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val appAnalytics: AppAnalytics
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isAscending = MutableStateFlow(true)
    val isAscending: StateFlow<Boolean> = _isAscending

    private val retryTrigger = MutableStateFlow(0)

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    val uiState: StateFlow<ListUiState<List<Category>>> = retryTrigger.flatMapLatest {
        combine(
            getCategoriesUseCase(),
            _searchQuery,
            _isAscending
        ) { categories, query, ascending ->
            val filtered = if (query.isBlank()) {
                categories
            } else {
                categories.filter { it.name.contains(query, ignoreCase = true) }
            }
            val sorted = if (ascending) {
                filtered.sortedBy { it.name.lowercase() }
            } else {
                filtered.sortedByDescending { it.name.lowercase() }
            }
            ListUiState.Success(sorted) as ListUiState<List<Category>>
        }.catch { e ->
            emit(ListUiState.Error(e.message ?: "Не удалось загрузить категории"))
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

    fun addCategory(name: String, description: String?, color: Int) {
        viewModelScope.launch {
            runCatching {
                addCategoryUseCase(Category(name = name, description = description, color = color))
            }.onSuccess {
                appAnalytics.logCategoryCreated(name)
            }.onFailure {
                appAnalytics.logHandledError("Не удалось создать категорию", it)
                _snackbarMessage.emit(it.message ?: "Не удалось создать категорию")
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            runCatching {
                updateCategoryUseCase(category)
            }.onSuccess {
                appAnalytics.logCategoryUpdated()
            }.onFailure {
                appAnalytics.logHandledError("Не удалось обновить категорию", it)
                _snackbarMessage.emit(it.message ?: "Не удалось обновить категорию")
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            runCatching {
                deleteCategoryUseCase(category)
            }.onSuccess {
                appAnalytics.logCategoryDeleted()
            }.onFailure {
                appAnalytics.logHandledError("Не удалось удалить категорию", it)
                _snackbarMessage.emit(it.message ?: "Не удалось удалить категорию")
            }
        }
    }
}
