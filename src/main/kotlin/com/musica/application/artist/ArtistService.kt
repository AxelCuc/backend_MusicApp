package com.musica.application.artist

import com.musica.domain.port.ArtistRepository
import java.util.UUID

class ArtistService(
    private val artistRepository: ArtistRepository
) {
    suspend fun createArtist(name: String, genre: String?) =
        artistRepository.create(name, genre)

    suspend fun getArtists() =
        artistRepository.findAll()

    suspend fun getArtistById(id: UUID) =
        artistRepository.findById(id)

    suspend fun getArtistWithRelations(id: UUID) =
        artistRepository.findByIdWithRelations(id)

    suspend fun deleteArtist(id: UUID) =
        artistRepository.delete(id)
}