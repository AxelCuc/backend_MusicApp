package com.musica.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager

object DatabaseFactory {

    init {
        // Cargar driver de PostgreSQL
        Class.forName("org.postgresql.Driver")
    }

    private fun getConnection(): Connection {
        val url = "jdbc:postgresql://localhost:5432/musica_db"
        val user = "postgres"
        val password = "0805"  // ¡CAMBIAR ESTO!

        return DriverManager.getConnection(url, user, password)
    }

    suspend fun <T> dbQuery(block: suspend (Connection) -> T): T =
        withContext(Dispatchers.IO) {
            getConnection().use { connection ->
                block(connection)
            }
        }
}