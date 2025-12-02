package com.musica.plugins

import com.musica.routes.artistRoutes
import com.musica.routes.albumRoutes
import com.musica.routes.trackRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }

    routing {
        // Health check endpoint
        get("/health") {
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "status" to "healthy",
                    "service" to "music-api",
                    "timestamp" to System.currentTimeMillis()
                )
            )
        }

        // API Documentation
        get("/api/docs") {
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "api" to "Music Catalog API",
                    "version" to "1.0.0",
                    "endpoints" to listOf(
                        mapOf("method" to "POST", "path" to "/api/artistas", "description" to "Crear artista"),
                        mapOf("method" to "GET", "path" to "/api/artistas", "description" to "Listar artistas"),
                        mapOf("method" to "GET", "path" to "/api/artistas/{id}", "description" to "Obtener artista con álbumes"),
                        mapOf("method" to "PUT", "path" to "/api/artistas/{id}", "description" to "Actualizar artista"),
                        mapOf("method" to "DELETE", "path" to "/api/artistas/{id}", "description" to "Eliminar artista"),
                        mapOf("method" to "POST", "path" to "/api/albumes", "description" to "Crear álbum"),
                        mapOf("method" to "GET", "path" to "/api/albumes", "description" to "Listar álbumes"),
                        mapOf("method" to "GET", "path" to "/api/albumes/{id}", "description" to "Obtener álbum con tracks"),
                        mapOf("method" to "GET", "path" to "/api/albumes/artista/{artistId}", "description" to "Álbumes por artista"),
                        mapOf("method" to "PUT", "path" to "/api/albumes/{id}", "description" to "Actualizar álbum"),
                        mapOf("method" to "DELETE", "path" to "/api/albumes/{id}", "description" to "Eliminar álbum"),
                        mapOf("method" to "POST", "path" to "/api/tracks", "description" to "Crear track"),
                        mapOf("method" to "GET", "path" to "/api/tracks", "description" to "Listar tracks"),
                        mapOf("method" to "GET", "path" to "/api/tracks/{id}", "description" to "Obtener track"),
                        mapOf("method" to "GET", "path" to "/api/tracks/album/{albumId}", "description" to "Tracks por álbum"),
                        mapOf("method" to "PUT", "path" to "/api/tracks/{id}", "description" to "Actualizar track"),
                        mapOf("method" to "DELETE", "path" to "/api/tracks/{id}", "description" to "Eliminar track")
                    )
                )
            )
        }

        // API Routes
        route("/api") {
            get {
                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "message" to "🎵 API de Catálogo Musical",
                        "version" to "1.0.0",
                        "author" to "Estudiante",
                        "documentation" to "/api/docs",
                        "health" to "/health"
                    )
                )
            }

            artistRoutes()
            albumRoutes()
            trackRoutes()
        }
    }
}