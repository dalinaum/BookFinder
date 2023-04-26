package io.github.dalinaum.bookfinder.entity

data class ImageLinks(
    val smallThumbnail: URL?,
    val thumbnail: URL?,
    val small: URL?,
    val medium: URL?,
    val large: URL?,
    val extraLarge: URL?
)

// http로 이미지를 가져오면 요청이 거부됨.
fun ImageLinks?.getThumbnail(): URL {
    this ?: return ""
    val candidate = thumbnail
        ?: smallThumbnail
        ?: small
        ?: medium
        ?: large
        ?: extraLarge
        ?: return ""
    return candidate.replace("http://", "https://")
}

fun ImageLinks?.getBigImage(): URL {
    this ?: return ""
    val candidate = extraLarge
        ?: large
        ?: medium
        ?: small
        ?: smallThumbnail
        ?: thumbnail
        ?: return ""
    return candidate.replace("http://", "https://")
}