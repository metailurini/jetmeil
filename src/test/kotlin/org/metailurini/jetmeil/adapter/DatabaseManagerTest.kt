package org.metailurini.jetmeil.adapter

import junit.framework.TestCase
import org.metailurini.jetmeil.adapter.DatabaseManager.Companion.SQLITE_URL

class DatabaseManagerTest : TestCase() {
    fun testWithEmptyURL() {
        val databaseManager = DatabaseManager()
        assertEquals(SQLITE_URL, databaseManager.url)
    }

    fun testWithNewURL() {
        val newURL = "jdbc:sqlite:/tmp/jetmeil.db"
        val databaseManager = DatabaseManager(newURL)
        assertEquals(newURL, databaseManager.url)
    }
}