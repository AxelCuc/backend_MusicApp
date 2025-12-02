package com.musica.routes

import com.musica.models.*
import com.musica.repositories.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.trackRoutes() {
    val trackRepository = TrackRepository()
    val albumRepository = AlbumRepository()

    route("/tracks") {
        // POST /api/tracks - Crear track
        post {
            try {
                val request = call.receive<TrackRequest>()

                // Validaciones
                if (request.title.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Validation Error", "El título es requerido", 400)
                    )
                    return@post
                }

                if (request.duration <= 0) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Validation Error", "Duración debe ser > 0", 400)
                    )
                    return@post
                }

                if (!isValidUUID(request.albumId)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Validation Error", "Album ID inválido", 400)
                    )
                    return@post
                }

                // Verificar que álbum existe
                val album = albumRepository.getAlbumById(request.albumId)
                if (album == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiError("Not Found", "Álbum no encontrado", 404)
                    )
                    return@post
                }

                val track = trackRepository.createTrack(request)

                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        data = mapOf(
                            "id" to track.id,
                            "title" to track.title,
                            "duration" to track.duration,
                            "albumId" to track.albumId
                        ),
                        status = 201,
                        message = "Track creado exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error creando track", 500)
                )
            }
        }

        // GET /api/tracks - Todos los tracks
        get {
            try {
                val tracks = trackRepository.getAllTracks()
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = tracks,
                        status = 200,
                        message = "Tracks obtenidos exitosamente"
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error obteniendo tracks", 500)
                )
            }
        }

        // GET /api/tracks/{id} - Track específico
        get("/{id}") {
            try {
                val id = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiError("Bad Request", "ID requerido", 400)
                )

                if (!isValidUUID(id)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Bad Request", "ID inválido", 400)
                    )
                    return@get
                }

                val track = trackRepository.getTrackById(id)
                if (track == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiError("Not Found", "Track no encontrado", 404)
                    )
                    return@get
                }

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = track,
                        status = 200,
                        message = "Track obtenido exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error obteniendo track", 500)
                )
            }
        }

        // GET /api/tracks/album/{albumId} - Tracks por álbum
        get("/album/{albumId}") {
            try {
                val albumId = call.parameters["albumId"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiError("Bad Request", "Album ID requerido", 400)
                )

                if (!isValidUUID(albumId)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Bad Request", "Album ID inválido", 400)
                    )
                    return@get
                }

                val tracks = trackRepository.getTracksByAlbum(albumId)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = tracks,
                        status = 200,
                        message = "Tracks obtenidos exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error obteniendo tracks", 500)
                )
            }
        }

        // PUT /api/tracks/{id} - Actualizar track
        put("/{id}") {
            try {
                val id = call.parameters["id"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ApiError("Bad Request", "ID requerido", 400)
                )

                if (!isValidUUID(id)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Bad Request", "ID inválido", 400)
                    )
                    return@put
                }

                val request = call.receive<TrackRequest>()
                val updatedTrack = trackRepository.updateTrack(id, request)

                if (updatedTrack == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiError("Not Found", "Track no encontrado", 404)
                    )
                    return@put
                }

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = updatedTrack,
                        status = 200,
                        message = "Track actualizado exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error actualizando track", 500)
                )
            }
        }

        // DELETE /api/tracks/{id} - Eliminar track
        delete("/{id}") {
            try {
                val id = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    ApiError("Bad Request", "ID requerido", 400)
                )

                if (!isValidUUID(id)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Bad Request", "ID inválido", 400)
                    )
                    return@delete
                }

                val deleted = trackRepository.deleteTrack(id)
                if (!deleted) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiError("Not Found", "Track no encontrado", 404)
                    )
                    return@delete
                }

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = null,
                        status = 200,
                        message = "Track eliminado exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error eliminando track", 500)
                )
            }
        }
    }
}

// Helper para validar UUID
private fun isValidUUID(uuid: String): Boolean {
    return try {
        UUID.fromString(uuid)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}