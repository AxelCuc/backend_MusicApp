package com.musica.infrastructure.web.routing

import com.musica.application.track.TrackService
import com.musica.infrastructure.web.dto.CreateTrackRequest
import com.musica.infrastructure.web.dto.toResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.trackRoutes(trackService: TrackService) {

    route("/tracks") {

        // POST /api/tracks
        post {
            val request = call.receive<CreateTrackRequest>()

            val albumUuid = try {
                UUID.fromString(request.albumId)
            } catch (e: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "albumId debe ser un UUID válido"
                )
                return@post
            }

            val created = trackService.createTrack(
                title = request.title,
                duration = request.duration,
                albumId = albumUuid
            )

            call.respond(HttpStatusCode.Created, created.toResponse())
        }

        // GET /api/tracks
        get {
            val tracks = trackService.getTracks()
            call.respond(tracks.map { it.toResponse() })
        }

        // GET /api/tracks/{id}
        get("{id}") {
            val idParam = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Falta id")

            val id = try {
                UUID.fromString(idParam)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest, "id inválido")
            }

            val track = trackService.getTrackById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(track.toResponse())
        }

        // DELETE /api/tracks/{id}
        delete("{id}") {
            val idParam = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Falta id")

            val id = try {
                UUID.fromString(idParam)
            } catch (e: IllegalArgumentException) {
                return@delete call.respond(HttpStatusCode.BadRequest, "id inválido")
            }

            val deleted = trackService.deleteTrack(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}