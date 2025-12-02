package com.musica.models

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: String,
    val title: String,
    val duration: Int,
    val albumId: String,
    val createdAt: String? = null
)

@Serializable
data class TrackRequest(
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