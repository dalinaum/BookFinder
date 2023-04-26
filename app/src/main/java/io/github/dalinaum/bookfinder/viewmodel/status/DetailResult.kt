package io.github.dalinaum.bookfinder.viewmodel.status

sealed class DetailResult<out T : Any> {
    object InitialState : DetailResult<Nothing>()
    data class Error(val exception: Exception) : DetailResult<Nothing>()
    data class Success<out T : Any>(val value: T) : DetailResult<T>()
}