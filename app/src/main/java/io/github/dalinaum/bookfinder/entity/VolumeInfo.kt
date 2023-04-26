package io.github.dalinaum.bookfinder.entity

data class VolumeInfo(
    val title: String,
    val subtitle: String?,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String,
    val description: String,
    val industryIdentifiers: List<IndustryIdentifier>,
    val readingModes: ReadingModes,
    val pageCount: Int?,
    val printType: String,
    val mainCategory: String?,
    val categories: List<String>?,
    val averageRating: Double?,
    val ratingsCount: Int?,
    val maturityRating: String,
    val allowAnonLogging: Boolean,
    val contentVersion: String,
    val panelizationSummary: PanelizationSummary,
    val imageLinks: ImageLinks?,
    val language: String,
    val previewLink: URL,
    val infoLink: URL,
    val canonicalVolumeLink: URL
)