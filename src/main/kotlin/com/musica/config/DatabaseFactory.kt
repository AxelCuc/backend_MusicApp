package com.musica.config

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {

    fun init() {
        val config = ConfigFactory.load()
        val dbConfig = config.getConfig("db")

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = dbConfig.getString("jdbcUrl")
            driverClassName = dbConfig.getString("driver")
            username = dbConfig.getString("user")
            password = dbConfig.getString("password")
            maximumPoolSize = dbConfig.getInt("maximumPoolSize")
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
    }
}