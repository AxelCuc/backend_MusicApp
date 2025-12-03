package com.musica.domain.port

import com.musica.domain.model.Artist
import com.musica.domain.model.Album
import com.musica.domain.model.Track
import java.util.UUID

interface ArtistRepository {
    suspend fun create(name: String, genre: String?): Artist
    suspend fun findAll(): List<Artist>
    suspend fun findById(id: UUID): Artist?
    suspend fun delete(id: UUID): Boolean

    suspend fun findByIdWithRelations(id: UUID): ArtistWithRelations?

    data class ArtistWithRelations(
        val artist: Artist,
        val albums: List<Album>,
        val tracks: List<Track>
    )
}