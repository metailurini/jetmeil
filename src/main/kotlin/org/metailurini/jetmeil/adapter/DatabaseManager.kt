package org.metailurini.jetmeil.adapter

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.metailurini.jetmeil.BookmarkQueries
import org.metailurini.jetmeil.Database
import org.sqlite.SQLiteException

class DatabaseManager(internal var dbPath: String? = null) {
    lateinit var driver: SqlDriver
    lateinit var database: Database

    companion object {
        @Volatile
        private var instance: DatabaseManager? = null

        private const val SQLITE_CLASS = "org.sqlite.JDBC"
        private const val CREATED_TABLE_MSG = "already exists"
        private const val PREFIX_JDBC_SQLITE = "jdbc:sqlite:"
        internal const val SQLITE_URL = "$PREFIX_JDBC_SQLITE/var/tmp/jetmeil.db"

        private fun getInstance(): DatabaseManager {
            return instance ?: synchronized(this) {
                instance ?: DatabaseManager().also { instance = it }
            }
        }

        fun getBookmarkQueries(): BookmarkQueries {
            return getInstance().database.bookmarkQueries
        }
    }

    init {
        connectDB()
    }

    private fun connectDB() {
        dbPath = if (dbPath == null) {
            SQLITE_URL
        } else {
            "$PREFIX_JDBC_SQLITE$dbPath"
        }

        Class.forName(SQLITE_CLASS)
        driver = JdbcSqliteDriver(dbPath!!)
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

    fun getBookmarkQueries(): BookmarkQueries {
        return database.bookmarkQueries
    }
}