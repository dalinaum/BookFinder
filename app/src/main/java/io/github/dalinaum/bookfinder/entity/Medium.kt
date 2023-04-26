package io.github.dalinaum.bookfinder.entity

typealias URL = String

data class Medium(
    val isAvailable: Boolean,
    val acsTokenLink: URL?
)

