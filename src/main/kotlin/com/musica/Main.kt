package com.musica

import com.musica.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 3000, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    DatabaseFactory.init()

    println("==========================================")
    println("🚀 Servidor de Música iniciado en puerto 3000")
    println("📚 API disponible en: http://localhost:3000/api")
    println("🏥 Health check: http://localhost:3000/health")
    println("==========================================")
}
