package com.musica.domain.model

import java.util.UUID

data class Album(
    val id: UUID,
    val title: String,
    val releaseYear: Int,
    val artistId: UUID
)