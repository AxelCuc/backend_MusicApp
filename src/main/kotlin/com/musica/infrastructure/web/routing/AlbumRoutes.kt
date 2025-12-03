package com.musica.infrastructure.web.routing

import com.musica.application.album.AlbumService
import com.musica.infrastructure.web.dto.CreateAlbumRequest
import com.musica.infrastructure.web.dto.toResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.albumRoutes(albumService: AlbumService) {

    route("/albumes") {

        // POST /api/albumes
        post {
            val request = call.receive<CreateAlbumRequest>()

            val artistUuid = try {
                UUID.fromString(request.artistId)
            } catch (e: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "artistId debe ser un UUID válido"
                )
                return@post
            }

            val created = albumService.createAlbum(
                title = request.title,
                releaseYear = request.releaseYear,
                artistId = artistUuid
            )

            // 201 (el test acepta 200 o 201)
            call.respond(HttpStatusCode.Created, created.toResponse())
        }

        // GET /api/albumes
        get {
            val albums = albumService.getAlbums()
            call.respond(albums.map { it.toResponse() })
        }

        // GET /api/albumes/{id}
        get("{id}") {
            val idParam = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Falta id")

            val id = try {
                UUID.fromString(idParam)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest, "id inválido")
            }

            val album = albumService.getAlbumById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(album.toResponse())
        }

        // DELETE /api/albumes/{id}
        delete("{id}") {
            val idParam = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Falta id")

            val id = try {
                UUID.fromString(idParam)
            } catch (e: IllegalArgumentException) {
                return@delete call.respond(HttpStatusCode.BadRequest, "id inválido")
            }

            val deleted = albumService.deleteAlbum(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}