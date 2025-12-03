package com.musica.infrastructure.persistence

import com.musica.domain.model.Album
import com.musica.domain.model.Artist
import com.musica.domain.model.Track
import com.musica.domain.port.ArtistRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
import java.util.UUID

class ArtistRepositoryImpl : ArtistRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction { block() }

    override suspend fun create(name: String, genre: String?): Artist = dbQuery {
        val now = Instant.now()
        val insertedId = ArtistTable.insert { row ->
            row[ArtistTable.name] = name
            row[ArtistTable.genre] = genre
            row[ArtistTable.createdAt] = now
            row[ArtistTable.updatedAt] = now
        }[ArtistTable.id]

        Artist(insertedId, name, genre)
    }

    override suspend fun findAll(): List<Artist> = dbQuery {
        ArtistTable
            .selectAll()
            .map { row ->
                Artist(
                    id = row[ArtistTable.id],
                    name = row[ArtistTable.name],
                    genre = row[ArtistTable.genre]
                )
            }
    }

    override suspend fun findById(id: UUID): Artist? = dbQuery {
        ArtistTable
            .select { ArtistTable.id eq id }
            .map { row ->
                Artist(
                    id = row[ArtistTable.id],
                    name = row[ArtistTable.name],
                    genre = row[ArtistTable.genre]
                )
            }
            .singleOrNull()
    }

    override suspend fun delete(id: UUID): Boolean = dbQuery {
        ArtistTable.deleteWhere { ArtistTable.id eq id } > 0
    }

    override suspend fun findByIdWithRelations(
        id: UUID
    ): ArtistRepository.ArtistWithRelations? = dbQuery {
        val artist = findById(id) ?: return@dbQuery null

        val albums = AlbumTable
            .select { AlbumTable.artistId eq id }
            .map { row ->
                Album(
                    id = row[AlbumTable.id],
                    title = row[AlbumTable.title],
                    releaseYear = row[AlbumTable.releaseYear],
                    artistId = row[AlbumTable.artistId]
                )
            }

        val tracks = TrackTable
            .innerJoin(AlbumTable, { albumId }, { AlbumTable.id })
            .select { AlbumTable.artistId eq id }
            .map { row ->
                Track(
                    id = row[TrackTable.id],
                    title = row[TrackTable.title],
                    duration = row[TrackTable.duration],
                    albumId = row[TrackTable.albumId]
                )
            }

        ArtistRepository.ArtistWithRelations(
            artist = artist,
            albums = albums,
            tracks = tracks
        )
    }
}