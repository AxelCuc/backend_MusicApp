package com.musica

import com.musica.application.album.AlbumService
import com.musica.application.artist.ArtistService
import com.musica.application.track.TrackService
import com.musica.config.DatabaseFactory
import com.musica.domain.port.AlbumRepository
import com.musica.domain.port.ArtistRepository
import com.musica.domain.port.TrackRepository
import com.musica.infrastructure.persistence.AlbumRepositoryImpl
import com.musica.infrastructure.persistence.ArtistRepositoryImpl
import com.musica.infrastructure.persistence.TrackRepositoryImpl
import com.musica.infrastructure.web.routing.albumRoutes
import com.musica.infrastructure.web.routing.artistRoutes
import com.musica.infrastructure.web.routing.trackRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

fun main(args: Array<String>): Unit =
    EngineMain.main(args)

fun Application.module() {

    // DB
    DatabaseFactory.init()

    val artistRepository: ArtistRepository = ArtistRepositoryImpl()
    val albumRepository: AlbumRepository = AlbumRepositoryImpl()
    val trackRepository: TrackRepository = TrackRepositoryImpl()

    val artistService = ArtistService(artistRepository)
    val albumService = AlbumService(albumRepository)
    val trackService = TrackService(trackRepository)

    install(CallLogging) {
        level = Level.INFO
    }

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                encodeDefaults = true
            }
        )
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(
                text = "Error interno: ${cause.message}",
                status = io.ktor.http.HttpStatusCode.InternalServerError
            )
            throw cause
        }
    }

    routing {
        route("/api") {
            artistRoutes(artistService)
            albumRoutes(albumService)
            trackRoutes(trackService)
        }
    }
}