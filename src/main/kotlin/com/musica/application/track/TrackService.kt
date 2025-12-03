package com.musica.application.track

import com.musica.domain.port.TrackRepository
import java.util.UUID

class TrackService(
    private val trackRepository: TrackRepository
) {
    suspend fun createTrack(title: String, duration: Int, albumId: UUID) =
        trackRepository.create(title, duration, albumId)

    suspend fun getTracks() = trackRepository.findAll()
    suspend fun getTrackById(id: UUID) = trackRepository.findById(id)
    suspend fun deleteTrack(id: UUID) = trackRepository.delete(id)
}