package com.musica.domain.port

import com.musica.domain.model.Album
import java.util.UUID

interface AlbumRepository {
    suspend fun create(title: String, releaseYear: Int, artistId: UUID): Album
    suspend fun findAll(): List<Album>
    suspend fun findById(id: UUID): Album?
    suspend fun delete(id: UUID): Boolean
}