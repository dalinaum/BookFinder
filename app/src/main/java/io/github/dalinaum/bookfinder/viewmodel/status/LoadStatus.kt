package io.github.dalinaum.bookfinder.viewmodel.status

sealed class LoadStatus {
    object IDLE : LoadStatus()
    object LOADING : LoadStatus()
    object APPENDING : LoadStatus()
    data class ERROR(val exception: Exception) : LoadStatus()
}