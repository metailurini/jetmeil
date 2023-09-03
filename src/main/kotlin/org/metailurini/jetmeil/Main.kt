package org.metailurini.jetmeil

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import kotlinx.coroutines.DelicateCoroutinesApi
import org.metailurini.jetmeil.adapter.DatabaseManager
import org.metailurini.jetmeil.adapter.GitCommander
import org.metailurini.jetmeil.adapter.GitCommanderImpl
import org.metailurini.jetmeil.plugins.listener.BookmarksListener
import org.metailurini.jetmeil.plugins.svoice.Svoice
import org.metailurini.jetmeil.plugins.svoice.SvoiceRepositoryImpl
import org.metailurini.jetmeil.Project as JetmeilProject


val ProjectNotFound = Exception("project not found")

class Main {
    companion object {
        @OptIn(DelicateCoroutinesApi::class)
        var svoice = Svoice(SvoiceRepositoryImpl())
        var gitter = GitCommanderImpl()
        var database = DatabaseManager.getActionQueries()

        lateinit var jetmeilProject: JetmeilProject
    }

    class JetmeilBookMarks : BookmarksListener(database, jetmeilProject)

    class JetmeilProjectManager : ProjectManagerListener {
        override fun projectOpened(project: Project) {
            val basePath = project.basePath ?: return
            jetmeilProject = loadProject(database, gitter, basePath)
        }
    }

    class SvoiceAction : AnAction() {
        @OptIn(DelicateCoroutinesApi::class)
        override fun actionPerformed(event: AnActionEvent) {
            try {
                svoice.actionPerformed(event)
            } catch (e: Exception) {
                println("@ svoice.actionPerformed $e")
            }
        }
    }
}

private operator fun Any.component0() {}

internal fun loadProject(database: ActionQueries, gitter: GitCommander, projectPath: String): JetmeilProject {
    var projectID: Long? = null

    var project = database.GetProjectByPath(projectPath).executeAsOneOrNull()
    if (project != null) {
        projectID = project.project_id
    }

    val githubLink = gitter.getRemoteURL(projectPath)
    database.UpsertProject(
        project_id = projectID,
        project_path = projectPath,
        github_link = githubLink
    )

    project = database.GetProjectByPath(projectPath).executeAsOneOrNull()
    if (project == null) {
        throw ProjectNotFound
    }

    return project
}

/*
                      +-----+      +-----------+
                      | APP | ---> | MIGRATION |
                      +-----+      +-----------+
                                        |
                    +---------+         |
               +----| SQLITE3 | <-------+
               |    +---------+
               |         |
               |         |
               |         v
               |    +------------+------------+--[BOOKMARK]-+-----------+-------------+-----------+          | e.groupRenamed -> update group name for whole project
               |    | project_id | group_name | description | file_path | line_number | commit_id |  <-------| e.groupRemoved -> remove by group name
               |    +------------+------------+-------------+-----------+-------------+-----------+          | e.bookmarkAdded -> insert bookmark
               |          |   \____________________(PK)_________|____________/                               | e.bookmarkChanged -> update bookmark
               |          |
               |          v
               |    +------------+---[PROJECT]--+--------------+-------------+
               +--> | project_id | project name | project_path | github_link |
                    +------------+--------------+--------------+-------------+
*/