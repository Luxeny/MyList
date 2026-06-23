package com.example.mylist.presentation.common

sealed interface ListUiState<out T> {
    data object Loading : ListUiState<Nothing>
    data class Success<T>(val data: T) : ListUiState<T>
    data class Error(val message: String) : ListUiState<Nothing>
}
