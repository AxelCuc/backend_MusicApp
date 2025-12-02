package com.musica.routes

import com.musica.models.*
import com.musica.repositories.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.artistRoutes() {
    val artistRepository = ArtistRepository()
    val albumRepository = AlbumRepository()
    val trackRepository = TrackRepository()

    route("/artistas") {
        // POST /api/artistas - Crear artista
        post {
            try {
                val request = call.receive<ArtistRequest>()

                if (request.name.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Validation Error", "El nombre es requerido", 400)
                    )
                    return@post
                }

                val artist = artistRepository.createArtist(request)

                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        data = mapOf(
                            "id" to artist.id,
                            "name" to artist.name,
                            "genre" to artist.genre
                        ),
                        status = 201,
                        message = "Artista creado exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error creando artista", 500)
                )
            }
        }

        // GET /api/artistas - Todos los artistas
        get {
            try {
                val artists = artistRepository.getAllArtists()
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = artists,
                        status = 200,
                        message = "Artistas obtenidos exitosamente"
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error obteniendo artistas", 500)
                )
            }
        }

        // GET /api/artistas/{id} - Artista con relaciones
        get("/{id}") {
            try {
                val id = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiError("Bad Request", "ID requerido", 400)
                )

                // Validar UUID
                if (!isValidUUID(id)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError("Bad Request", "ID inválido", 400)
                    )
                    return@get
                }

                val artist = artistRepository.getArtistById(id)
                if (artist == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiError("Not Found", "Artista no encontrado", 404)
                    )
                    return@get
                }

                // Obtener álbumes con tracks
                val albums = albumRepository.getAlbumsByArtist(id)
                val albumResponses = mutableListOf<AlbumResponse>()

                for (album in albums) {
                    val tracks = trackRepository.getTracksByAlbum(album.id)
                    val trackResponses = tracks.map { track ->
                        TrackResponse(
                            id = track.id,
                            title = track.title,
                            duration = track.duration,
                            albumId = track.albumId
                        )
                    }

                    albumResponses.add(
                        AlbumResponse(
                            id = album.id,
                            title = album.title,
                            releaseYear = album.releaseYear,
                            artistId = album.artistId,
                            tracks = trackResponses
                        )
                    )
                }

                val response = ArtistResponse(
                    id = artist.id,
                    name = artist.name,
                    genre = artist.genre,
                    albumes = albumResponses
                )

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = response,
                        status = 200,
                        message = "Artista obtenido exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error obteniendo artista", 500)
                )
            }
        }

        // PUT /api/artistas/{id} - Actualizar artista
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

                val request = call.receive<ArtistRequest>()
                val updatedArtist = artistRepository.updateArtist(id, request)

                if (updatedArtist == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiError("Not Found", "Artista no encontrado", 404)
                    )
                    return@put
                }

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = updatedArtist,
                        status = 200,
                        message = "Artista actualizado exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error actualizando artista", 500)
                )
            }
        }

        // DELETE /api/artistas/{id} - Eliminar artista
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

                // Verificar si tiene álbumes
                val hasAlbums = artistRepository.artistHasAlbums(id)
                if (hasAlbums) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        ApiError(
                            "Conflict",
                            "No se puede eliminar: artista tiene álbumes asociados",
                            409
                        )
                    )
                    return@delete
                }

                val deleted = artistRepository.deleteArtist(id)
                if (!deleted) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiError("Not Found", "Artista no encontrado", 404)
                    )
                    return@delete
                }

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        data = null,
                        status = 200,
                        message = "Artista eliminado exitosamente"
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiError("Server Error", "Error eliminando artista", 500)
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