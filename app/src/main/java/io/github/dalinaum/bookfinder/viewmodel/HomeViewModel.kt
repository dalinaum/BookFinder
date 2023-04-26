package io.github.dalinaum.bookfinder.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.dalinaum.bookfinder.api.GoogleBooksService
import io.github.dalinaum.bookfinder.entity.Item
import io.github.dalinaum.bookfinder.viewmodel.status.LoadStatus
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val googleBooksService: GoogleBooksService
) : ViewModel() {
    private var startIndex = 0

    var currentQuery by mutableStateOf("")
    var canPaginate by mutableStateOf(false)
    var loadStatus by mutableStateOf(LoadStatus.IDLE)
    val volumeList = mutableStateListOf<Item>()

    fun loadData(
        startIndex: Int = this.startIndex
    ) = viewModelScope.launch {
        loadStatus = if (startIndex == 0) {
            volumeList.clear()
            LoadStatus.LOADING
        } else {
            LoadStatus.APPENDING
        }
        loadStatus = try {
            googleBooksService.getVolumes(
                query = currentQuery,
                startIndex = startIndex
            ).items.let { items ->
                volumeList.addAll(items)
                val nextStartIndex = startIndex + items.count()
                this@HomeViewModel.startIndex = nextStartIndex
                canPaginate = items.isNotEmpty()
                LoadStatus.IDLE
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message ?: "")
            e.printStackTrace()
            canPaginate = false
            LoadStatus.ERROR
        }
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}
