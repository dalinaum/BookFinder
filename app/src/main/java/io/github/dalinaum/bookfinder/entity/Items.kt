package io.github.dalinaum.bookfinder.entity

data class Items(
    val kind: String,
    val totalItems: Long,
    val items: List<Item>
)