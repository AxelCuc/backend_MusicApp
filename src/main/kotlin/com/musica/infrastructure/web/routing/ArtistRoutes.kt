package com.musica.infrastructure.web.routing

import com.musica.application.artist.ArtistService
import com.musica.infrastructure.web.dto.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.artistRoutes(artistService: ArtistService) {

    route("/artistas") {

        // POST /api/artistas
        post {
            val request = call.receive<CreateArtistRequest>()
            val created = artistService.createArtist(request.name, request.genre)
            call.respond(HttpStatusCode.Created, created.toResponse())
        }

        // GET /api/artistas
        get {
            val artists = artistService.getArtists()
            call.respond(artists.map { it.toResponse() })
        }

        // GET /api/artistas/{id} (con relaciones)
        get("{id}") {
            val idParam = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Falta id")

            val id = try {
                UUID.fromString(idParam)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest, "id inválido")
            }

            val artistWithRelations = artistService.getArtistWithRelations(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            val response = ArtistWithRelationsResponse(
                id = artistWithRelations.artist.id.toString(),
                name = artistWithRelations.artist.name,
                genre = artistWithRelations.artist.genre,
                albums = artistWithRelations.albums.map { it.toResponse() },
                tracks = artistWithRelations.tracks.map { it.toResponse() }
            )

            call.respond(response)
        }

        // DELETE /api/artistas/{id}
        delete("{id}") {
            val idParam = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Falta id")

            val id = try {
                UUID.fromString(idParam)
            } catch (e: IllegalArgumentException) {
                return@delete call.respond(HttpStatusCode.BadRequest, "id inválido")
            }

            val deleted = artistService.deleteArtist(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}