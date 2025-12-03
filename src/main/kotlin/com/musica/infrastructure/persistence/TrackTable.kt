package com.musica.infrastructure.persistence

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.UUID

object TrackTable : Table("tracks") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val title = varchar("title", 150)
    val duration = integer("duration")
    val albumId = uuid("album_id")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
}