package org.metailurini.jetmeil.adapter

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.metailurini.jetmeil.ActionQueries
import org.metailurini.jetmeil.Database
import org.sqlite.SQLiteException

class DatabaseManager private constructor() {

    private val driver: SqlDriver
    private val sqliteURL = "jdbc:sqlite:/var/tmp/jetmeil.db"

    val database: Database

    companion object {
        @Volatile
        private var instance: DatabaseManager? = null

        private const val SQLITE_CLASS = "org.sqlite.JDBC"
        private const val CREATED_TABLE_MSG = "already exists"

        fun getInstance(): DatabaseManager {
            return instance ?: synchronized(this) {
                instance ?: DatabaseManager().also { instance = it }
            }
        }

        fun getActionQueries(): ActionQueries {
            return getInstance().database.actionQueries
        }
    }

    init {
        Class.forName(SQLITE_CLASS)
        driver = JdbcSqliteDriver(sqliteURL)
        try {
            Database.Schema.create(driver)
        } catch (e: SQLiteException) {
            if (e.message?.contains(CREATED_TABLE_MSG) == false) {
                throw e
            }
            println(e.message)
        }
        database = Database(driver)
    }
}