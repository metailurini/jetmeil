package org.metailurini.jetmeil.adapter.repository

import org.metailurini.jetmeil.BookmarkQueries
import org.metailurini.jetmeil.Project

interface BookmarkRepository : BookmarkQueries {
    fun getProjectsByPath(projectPath: String): List<Project>
    fun getProjects(): List<Project>
}