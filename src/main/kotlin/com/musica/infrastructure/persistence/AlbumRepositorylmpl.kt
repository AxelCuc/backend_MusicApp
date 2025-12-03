package com.musica.infrastructure.persistence

import com.musica.domain.model.Album
import com.musica.domain.port.AlbumRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
import java.util.UUID

class AlbumRepositoryImpl : AlbumRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction { block() }

    override suspend fun create(title: String, releaseYear: Int, artistId: UUID): Album = dbQuery {
        val now = Instant.now()
        val insertedId = AlbumTable.insert { row ->
            row[AlbumTable.title] = title
            row[AlbumTable.releaseYear] = releaseYear
            row[AlbumTable.artistId] = artistId
            row[AlbumTable.createdAt] = now
            row[AlbumTable.updatedAt] = now
        }[AlbumTable.id]

        Album(insertedId, title, releaseYear, artistId)
    }

    override suspend fun findAll(): List<Album> = dbQuery {
        AlbumTable
            .selectAll()
            .map { row ->
                Album(
                    id = row[AlbumTable.id],
                    title = row[AlbumTable.title],
                    releaseYear = row[AlbumTable.releaseYear],
                    artistId = row[AlbumTable.artistId]
                )
            }
    }

    override suspend fun findById(id: UUID): Album? = dbQuery {
        AlbumTable
            .select { AlbumTable.id eq id }
            .map { row ->
                Album(
                    id = row[AlbumTable.id],
                    title = row[AlbumTable.title],
                    releaseYear = row[AlbumTable.releaseYear],
                    artistId = row[AlbumTable.artistId]
                )
            }
            .singleOrNull()
    }

    override suspend fun delete(id: UUID): Boolean = dbQuery {
        AlbumTable.deleteWhere { AlbumTable.id eq id } > 0
    }
}