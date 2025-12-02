package com.musica.models

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: String,
    val title: String,
    val releaseYear: Int,
    val artistId: String,
    val createdAt: String? = null
)

@Serializable
data class AlbumRequest(
    val title: String,
    val releaseYear: Int,
    val artistId: String
)

@Serializable
data class AlbumResponse(
    val id: String,
    val title: String,
    val releaseYear: Int,
    val artistId: String,
    val tracks: List<TrackResponse> = emptyList()
)