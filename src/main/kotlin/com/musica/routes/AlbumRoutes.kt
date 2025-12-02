package com.musica.routes

import com.musica.models.*
import com.musica.repositories.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.albumRoutes() {
    val albumRepository = AlbumRepository()
    val artistRepository = ArtistRepository()
    val trackRepository = TrackRepository()

    route("/albumes") {
        // POST /api/albumes - Crear álbum
        post {
            try {
                val request = call.receive<AlbumRequest>()

                // Validaciones
                if (request.title.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Validation Error", "El título es requerido", 400)
                    )
                    return@post
                }

                if (request.releaseYear < 1900) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Validation Error", "Año inválido", 400)
                    )
                    return@post
                }

                if (!isValidUUID(request.artistId)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Validation Error", "Artist ID inválido", 400)
                    )
                    return@post
                }

                // Verificar que artista existe
                val artist = artistRepository.getArtistById(request.artistId)
                if (artist == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiError("Not Found", "Artista no encontrado", 404)
                    )
                    return@post
                }

                val album = albumRepository.createAlbum(request)

                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        data = mapOf(
                            "id" to album.id,
                            "title" to album.title,
                            "releaseYear" to album.releaseYear,
                            "artistId" to album.artistId
                        ),
                        status = 201,
                        message = "Álbum creado exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error creando álbum", 500)
                )
            }
        }

        // GET /api/albumes - Todos los álbumes
        get {
            try {
                val albums = albumRepository.getAllAlbums()
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = albums,
                        status = 200,
                        message = "Álbumes obtenidos exitosamente"
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error obteniendo álbumes", 500)
                )
            }
        }

        // GET /api/albumes/{id} - Álbum específico
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

                val album = albumRepository.getAlbumById(id)
                if (album == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiError("Not Found", "Álbum no encontrado", 404)
                    )
                    return@get
                }

                // Obtener tracks del álbum
                val tracks = trackRepository.getTracksByAlbum(id)
                val trackResponses = tracks.map { track ->
                    TrackResponse(
                        id = track.id,
                        title = track.title,
                        duration = track.duration,
                        albumId = track.albumId
                    )
                }

                val response = AlbumResponse(
                    id = album.id,
                    title = album.title,
                    releaseYear = album.releaseYear,
                    artistId = album.artistId,
                    tracks = trackResponses
                )

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = response,
                        status = 200,
                        message = "Álbum obtenido exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error obteniendo álbum", 500)
                )
            }
        }

        // GET /api/albumes/artista/{artistId} - Álbumes por artista
        get("/artista/{artistId}") {
            try {
                val artistId = call.parameters["artistId"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiError("Bad Request", "Artist ID requerido", 400)
                )

                if (!isValidUUID(artistId)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Bad Request", "Artist ID inválido", 400)
                    )
                    return@get
                }

                val albums = albumRepository.getAlbumsByArtist(artistId)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = albums,
                        status = 200,
                        message = "Álbumes obtenidos exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error obteniendo álbumes", 500)
                )
            }
        }

        // PUT /api/albumes/{id} - Actualizar álbum
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

                val request = call.receive<AlbumRequest>()
                val updatedAlbum = albumRepository.updateAlbum(id, request)

                if (updatedAlbum == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiError("Not Found", "Álbum no encontrado", 404)
                    )
                    return@put
                }

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = updatedAlbum,
                        status = 200,
                        message = "Álbum actualizado exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error actualizando álbum", 500)
                )
            }
        }

        // DELETE /api/albumes/{id} - Eliminar álbum
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

                // Verificar si tiene tracks
                val hasTracks = albumRepository.albumHasTracks(id)
                if (hasTracks) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        ApiError(
                            "Conflict",
                            "No se puede eliminar: álbum tiene tracks asociados",
                            409
                        )
                    )
                    return@delete
                }

                val deleted = albumRepository.deleteAlbum(id)
                if (!deleted) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiError("Not Found", "Álbum no encontrado", 404)
                    )
                    return@delete
                }

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = null,
                        status = 200,
                        message = "Álbum eliminado exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error eliminando álbum", 500)
                )
            }
        }
    }
}

// Helper para validar UUID (mismo que en ArtistRoutes)
private fun isValidUUID(uuid: String): Boolean {
    return try {
        UUID.fromString(uuid)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}