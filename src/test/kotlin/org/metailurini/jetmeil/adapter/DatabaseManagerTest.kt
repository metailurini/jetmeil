package org.metailurini.jetmeil.adapter

import junit.framework.TestCase
import org.metailurini.jetmeil.adapter.DatabaseManager.Companion.SQLITE_URL

class DatabaseManagerTest : TestCase() {
    fun testWithEmptyURL() {
        val databaseManager = DatabaseManager()
        assertEquals(SQLITE_URL, databaseManager.dbPath)
    }

    fun testWithNewURL() {
        val newURL = "/tmp/jetmeil.db"
        val databaseManager = DatabaseManager(newURL)
        assertEquals("jdbc:sqlite:/tmp/jetmeil.db", databaseManager.dbPath)
    }
}