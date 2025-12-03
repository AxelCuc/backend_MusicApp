package com.musica.infrastructure.web.dto

import com.musica.domain.model.Album
import kotlinx.serialization.Serializable

@Serializable
data class CreateAlbumRequest(
    val title: String,
    val releaseYear: Int,
    val artistId: String
)

@Serializable
data class AlbumResponse(
    val id: String,
    val title: String,
    val releaseYear: Int,
    val artistId: String
)

fun Album.toResponse() = AlbumResponse(
    id = id.toString(),
    title = title,
    releaseYear = releaseYear,
    artistId = artistId.toString()
)