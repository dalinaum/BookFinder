package io.github.dalinaum.bookfinder.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.dalinaum.bookfinder.api.GoogleBooksService
import io.github.dalinaum.bookfinder.entity.Item
import io.github.dalinaum.bookfinder.viewmodel.status.DetailResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val googleBooksService: GoogleBooksService
) : ViewModel() {
    val id: MutableStateFlow<String> = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val volume: Flow<DetailResult<Item>> = id.mapLatest {
        try {
            if (id.value.isBlank()) {
                throw IllegalArgumentException("잘못된 ID가 지정되었음.")
            }
            DetailResult.Success(googleBooksService.getVolume(it))
        } catch (exception: Exception) {
            DetailResult.Error(exception)
        }
    }
}