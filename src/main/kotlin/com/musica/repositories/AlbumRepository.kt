package com.musica.repositories

import com.musica.models.Album
import com.musica.models.AlbumRequest
import com.musica.plugins.DatabaseFactory
import java.util.UUID

class AlbumRepository {

    suspend fun createAlbum(request: AlbumRequest): Album {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                INSERT INTO albumes (title, release_year, artist_id, created_at)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                RETURNING id, title, release_year, artist_id,
                         TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
            """

            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, request.title)
                stmt.setInt(2, request.releaseYear)
                stmt.setObject(3, UUID.fromString(request.artistId))

                val rs = stmt.executeQuery()
                rs.next()

                Album(
                    id = rs.getString("id"),
                    title = rs.getString("title"),
                    releaseYear = rs.getInt("release_year"),
                    artistId = rs.getString("artist_id"),
                    createdAt = rs.getString("created_at")
                )
            }
        }
    }

    suspend fun getAlbumById(id: String): Album? {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                SELECT id, title, release_year, artist_id,
                       TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
                FROM albumes 
                WHERE id = ?
            """

            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, UUID.fromString(id))

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    Album(
                        id = rs.getString("id"),
                        title = rs.getString("title"),
                        releaseYear = rs.getInt("release_year"),
                        artistId = rs.getString("artist_id"),
                        createdAt = rs.getString("created_at")
                    )
                } else {
                    null
                }
            }
        }
    }

    suspend fun getAlbumsByArtist(artistId: String): List<Album> {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                SELECT id, title, release_year, artist_id,
                       TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
                FROM albumes 
                WHERE artist_id = ?
                ORDER BY release_year
            """

            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, UUID.fromString(artistId))

                val rs = stmt.executeQuery()
                val albums = mutableListOf<Album>()

                while (rs.next()) {
                    albums.add(
                        Album(
                            id = rs.getString("id"),
                            title = rs.getString("title"),
                            releaseYear = rs.getInt("release_year"),
                            artistId = rs.getString("artist_id"),
                            createdAt = rs.getString("created_at")
                        )
                    )
                }
                albums
            }
        }
    }

    suspend fun getAllAlbums(): List<Album> {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                SELECT id, title, release_year, artist_id,
                       TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
                FROM albumes 
                ORDER BY title
            """

            connection.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                val albums = mutableListOf<Album>()

                while (rs.next()) {
                    albums.add(
                        Album(
                            id = rs.getString("id"),
                            title = rs.getString("title"),
                            releaseYear = rs.getInt("release_year"),
                            artistId = rs.getString("artist_id"),
                            createdAt = rs.getString("created_at")
                        )
                    )
                }
                albums
            }
        }
    }

    suspend fun updateAlbum(id: String, request: AlbumRequest): Album? {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                UPDATE albumes 
                SET title = ?, release_year = ?, artist_id = ?
                WHERE id = ?
                RETURNING id, title, release_year, artist_id, created_at
            """

            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, request.title)
                stmt.setInt(2, request.releaseYear)
                stmt.setObject(3, UUID.fromString(request.artistId))
                stmt.setObject(4, UUID.fromString(id))

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    Album(
                        id = rs.getString("id"),
                        title = rs.getString("title"),
                        releaseYear = rs.getInt("release_year"),
                        artistId = rs.getString("artist_id"),
                        createdAt = rs.getString("created_at")
                    )
                } else {
                    null
                }
            }
        }
    }

    suspend fun deleteAlbum(id: String): Boolean {
        return DatabaseFactory.dbQuery { connection ->
            val sql = "DELETE FROM albumes WHERE id = ?"

            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, UUID.fromString(id))
                stmt.executeUpdate() > 0
            }
        }
    }

    suspend fun albumHasTracks(albumId: String): Boolean {
        return DatabaseFactory.dbQuery { connection ->
            val sql = "SELECT COUNT(*) as count FROM tracks WHERE album_id = ?"

            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, UUID.fromString(albumId))
                val rs = stmt.executeQuery()
                rs.next()
                rs.getInt("count") > 0
            }
        }
    }
}