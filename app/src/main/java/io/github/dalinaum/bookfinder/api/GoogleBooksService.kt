package io.github.dalinaum.bookfinder.api

import io.github.dalinaum.bookfinder.entity.Item
import io.github.dalinaum.bookfinder.entity.Items
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksService {
    @GET("volumes")
    suspend fun getVolumes(
        @Query("q") query: String,
        @Query("startIndex") startIndex: Int? = null,
        @Query("maxResults") maxResults: Int? = null
    ): Items

    @GET("volumes/{volumeId}")
    suspend fun getVolume(
        @Path("volumeId") volumeId: String
    ): Item
}