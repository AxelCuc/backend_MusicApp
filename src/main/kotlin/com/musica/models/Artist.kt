package com.musica.models

import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val genre: String? = null,
    val createdAt: String? = null
)

@Serializable
data class ArtistRequest(
    val name: String,
    val genre: String? = null
)

@Serializable
data class ArtistResponse(
    val id: String,
    val name: String,
    val genre: String?,
    val albumes: List<AlbumResponse> = emptyList()
)