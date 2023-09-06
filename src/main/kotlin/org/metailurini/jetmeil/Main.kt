package org.metailurini.jetmeil

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.metailurini.jetmeil.adapter.DatabaseManager
import org.metailurini.jetmeil.adapter.GitCommander
import org.metailurini.jetmeil.adapter.GitCommanderImpl
import org.metailurini.jetmeil.adapter.repository.BookmarkRepository
import org.metailurini.jetmeil.adapter.repository.BookmarkRepositoryImpl
import org.metailurini.jetmeil.plugins.bookmark.BookmarksListener
import org.metailurini.jetmeil.plugins.svoice.Svoice
import org.metailurini.jetmeil.plugins.svoice.SvoiceRepositoryImpl
import org.metailurini.jetmeil.Project as JetmeilProject


class Main {
    companion object {
        var svoice = Svoice(SvoiceRepositoryImpl())
        var gitter: GitCommander = GitCommanderImpl()
        var bookmarkRepo: BookmarkRepository = BookmarkRepositoryImpl(DatabaseManager.getBookmarkQueries())
        var projects: List<JetmeilProject> = listOf()
    }

    class JetmeilBookMarks : BookmarksListener(bookmarkRepo, gitter, getListProjects())

    class JetmeilPostStartupActivity : ProjectActivity {
        override suspend fun execute(project: Project) {
            upsertProject(project)
        }
    }


    class SvoiceAction : AnAction() {
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

fun getListProjects(): List<JetmeilProject> {
    if (Main.projects.isEmpty())
        Main.projects = Main.bookmarkRepo.getProjects()
    return Main.projects
}

fun upsertProject(project: Project) {
    // set empty project for reset projects list for bookmarks listener
    Main.projects = listOf()

    project.basePath?.let { projectPath ->
        val githubLink = Main.gitter.getRemoteURL(projectPath)
        var projectID: Long? = null
        val exisingProjects = Main.bookmarkRepo.getProjectsByPath(projectPath)

        if (exisingProjects.isNotEmpty()) {
            projectID = exisingProjects[0].project_id
        }

        Main.bookmarkRepo.UpsertProject(
            project_id = projectID,
            project_path = projectPath,
            github_link = githubLink
        )
    }
}