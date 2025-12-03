package com.musica.application.album

import com.musica.domain.port.AlbumRepository
import java.util.UUID

class AlbumService(
    private val albumRepository: AlbumRepository
) {
    suspend fun createAlbum(title: String, releaseYear: Int, artistId: UUID) =
        albumRepository.create(title, releaseYear, artistId)

    suspend fun getAlbums() = albumRepository.findAll()
    suspend fun getAlbumById(id: UUID) = albumRepository.findById(id)
    suspend fun deleteAlbum(id: UUID) = albumRepository.delete(id)
}