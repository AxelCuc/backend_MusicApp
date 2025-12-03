package com.musica.infrastructure.web.dto

import com.musica.domain.model.Track
import kotlinx.serialization.Serializable

@Serializable
data class CreateTrackRequest(
    val title: String,
    val duration: Int,
    val albumId: String
)

@Serializable
data class TrackResponse(
    val id: String,
    val title: String,
    val duration: Int,
    val albumId: String
)

fun Track.toResponse() = TrackResponse(
    id = id.toString(),
    title = title,
    duration = duration,
    albumId = albumId.toString()
)