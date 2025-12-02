package com.musica.repositories

import com.musica.models.Track
import com.musica.models.TrackRequest
import com.musica.plugins.DatabaseFactory
import java.util.UUID

class TrackRepository {

    suspend fun createTrack(request: TrackRequest): Track {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                INSERT INTO tracks (title, duration, album_id, created_at)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                RETURNING id, title, duration, album_id,
                         TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
            """

            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, request.title)
                stmt.setInt(2, request.duration)
                stmt.setObject(3, UUID.fromString(request.albumId))

                val rs = stmt.executeQuery()
                rs.next()

                Track(
                    id = rs.getString("id"),
                    title = rs.getString("title"),
                    duration = rs.getInt("duration"),
                    albumId = rs.getString("album_id"),
                    createdAt = rs.getString("created_at")
                )
            }
        }
    }

    suspend fun getTrackById(id: String): Track? {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                SELECT id, title, duration, album_id,
                       TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
                FROM tracks 
                WHERE id = ?
            """

            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, UUID.fromString(id))

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    Track(
                        id = rs.getString("id"),
                        title = rs.getString("title"),
                        duration = rs.getInt("duration"),
                        albumId = rs.getString("album_id"),
                        createdAt = rs.getString("created_at")
                    )
                } else {
                    null
                }
            }
        }
    }

    suspend fun getTracksByAlbum(albumId: String): List<Track> {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                SELECT id, title, duration, album_id,
                       TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
                FROM tracks 
                WHERE album_id = ?
                ORDER BY title
            """

            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, UUID.fromString(albumId))

                val rs = stmt.executeQuery()
                val tracks = mutableListOf<Track>()

                while (rs.next()) {
                    tracks.add(
                        Track(
                            id = rs.getString("id"),
                            title = rs.getString("title"),
                            duration = rs.getInt("duration"),
                            albumId = rs.getString("album_id"),
                            createdAt = rs.getString("created_at")
                        )
                    )
                }
                tracks
            }
        }
    }

    suspend fun getAllTracks(): List<Track> {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                SELECT id, title, duration, album_id,
                       TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
                FROM tracks 
                ORDER BY title
            """

            connection.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                val tracks = mutableListOf<Track>()

                while (rs.next()) {
                    tracks.add(
                        Track(
                            id = rs.getString("id"),
                            title = rs.getString("title"),
                            duration = rs.getInt("duration"),
                            albumId = rs.getString("album_id"),
                            createdAt = rs.getString("created_at")
                        )
                    )
                }
                tracks
            }
        }
    }

    suspend fun updateTrack(id: String, request: TrackRequest): Track? {
        return DatabaseFactory.dbQuery { connection ->
            val sql = """
                UPDATE tracks 
                SET title = ?, duration = ?, album_id = ?
                WHERE id = ?
                RETURNING id, title, duration, album_id, created_at
            """

            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, request.title)
                stmt.setInt(2, request.duration)
                stmt.setObject(3, UUID.fromString(request.albumId))
                stmt.setObject(4, UUID.fromString(id))

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    Track(
                        id = rs.getString("id"),
                        title = rs.getString("title"),
                        duration = rs.getInt("duration"),
                        albumId = rs.getString("album_id"),
                        createdAt = rs.getString("created_at")
                    )
                } else {
                    null
                }
            }
        }
    }

    suspend fun deleteTrack(id: String): Boolean {
        return DatabaseFactory.dbQuery { connection ->
            val sql = "DELETE FROM tracks WHERE id = ?"

            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, UUID.fromString(id))
                stmt.executeUpdate() > 0
            }
        }
    }
}