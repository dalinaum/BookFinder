package io.github.dalinaum.bookfinder.entity

data class Item(
    val kind: String,
    val id: String,
    val etag: String,
    val selfLink: URL,
    val volumeInfo: VolumeInfo,
    val saleInfo: SaleInfo,
    val accessInfo: AccessInfo
)

