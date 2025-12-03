package com.musica.infrastructure.persistence

import com.musica.domain.model.Track
import com.musica.domain.port.TrackRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
import java.util.UUID

class TrackRepositoryImpl : TrackRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction { block() }

    override suspend fun create(title: String, duration: Int, albumId: UUID): Track = dbQuery {
        val now = Instant.now()
        val insertedId = TrackTable.insert { row ->
            row[TrackTable.title] = title
            row[TrackTable.duration] = duration
            row[TrackTable.albumId] = albumId
            row[TrackTable.createdAt] = now
            row[TrackTable.updatedAt] = now
        }[TrackTable.id]

        Track(insertedId, title, duration, albumId)
    }

    override suspend fun findAll(): List<Track> = dbQuery {
        TrackTable
            .selectAll()
            .map { row ->
                Track(
                    id = row[TrackTable.id],
                    title = row[TrackTable.title],
                    duration = row[TrackTable.duration],
                    albumId = row[TrackTable.albumId]
                )
            }
    }

    override suspend fun findById(id: UUID): Track? = dbQuery {
        TrackTable
            .select { TrackTable.id eq id }
            .map { row ->
                Track(
                    id = row[TrackTable.id],
                    title = row[TrackTable.title],
                    duration = row[TrackTable.duration],
                    albumId = row[TrackTable.albumId]
                )
            }
            .singleOrNull()
    }

    override suspend fun delete(id: UUID): Boolean = dbQuery {
        TrackTable.deleteWhere { TrackTable.id eq id } > 0
    }
}