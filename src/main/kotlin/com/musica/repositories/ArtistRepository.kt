package com.musica.repositories

import com.musica.models.Artist
import com.musica.models.ArtistRequest
import com.musica.plugins.DatabaseFactory
import java.util.UUID

class ArtistRepository {

    suspend fun createArtist(request: ArtistRequest): Artist {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                INSERT INTO artistas (name, genre, created_at)
                VALUES (?, ?, CURRENT_TIMESTAMP)
                RETURNING id, name, genre, 
                         TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
            """

            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, request.name)
                stmt.setString(2, request.genre ?: "")

                val rs = stmt.executeQuery()
                rs.next()

                Artist(
                    id = rs.getString("id"),
                    name = rs.getString("name"),
                    genre = if (rs.getString("genre").isNullOrEmpty()) null else rs.getString("genre"),
                    createdAt = rs.getString("created_at")
                )
            }
        }
    }

    suspend fun getArtistById(id: String): Artist? {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                SELECT id, name, genre, 
                       TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
                FROM artistas 
                WHERE id = ?
            """

            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, UUID.fromString(id))

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    Artist(
                        id = rs.getString("id"),
                        name = rs.getString("name"),
                        genre = if (rs.getString("genre").isNullOrEmpty()) null else rs.getString("genre"),
                        createdAt = rs.getString("created_at")
                    )
                } else {
                    null
                }
            }
        }
    }

    suspend fun getAllArtists(): List<Artist> {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                SELECT id, name, genre, 
                       TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
                FROM artistas 
                ORDER BY name
            """

            connection.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                val artists = mutableListOf<Artist>()

                while (rs.next()) {
                    artists.add(
                        Artist(
                            id = rs.getString("id"),
                            name = rs.getString("name"),
                            genre = if (rs.getString("genre").isNullOrEmpty()) null else rs.getString("genre"),
                            createdAt = rs.getString("created_at")
                        )
                    )
                }
                artists
            }
        }
    }

    suspend fun updateArtist(id: String, request: ArtistRequest): Artist? {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                UPDATE artistas 
                SET name = ?, genre = ?
                WHERE id = ?
                RETURNING id, name, genre, created_at
            """

            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, request.name)
                stmt.setString(2, request.genre ?: "")
                stmt.setObject(3, UUID.fromString(id))

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    Artist(
                        id = rs.getString("id"),
                        name = rs.getString("name"),
                        genre = if (rs.getString("genre").isNullOrEmpty()) null else rs.getString("genre"),
                        createdAt = rs.getString("created_at")
                    )
                } else {
                    null
                }
            }
        }
    }

    suspend fun deleteArtist(id: String): Boolean {
        return DatabaseFactory.dbQuery { connection ->
            val sql = "DELETE FROM artistas WHERE id = ?"

            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, UUID.fromString(id))
                stmt.executeUpdate() > 0
            }
        }
    }

    suspend fun artistHasAlbums(artistId: String): Boolean {
        return DatabaseFactory.dbQuery { connection ->
            val sql = "SELECT COUNT(*) as count FROM albumes WHERE artist_id = ?"

            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, UUID.fromString(artistId))
                val rs = stmt.executeQuery()
                rs.next()
                rs.getInt("count") > 0
            }
        }
    }
}