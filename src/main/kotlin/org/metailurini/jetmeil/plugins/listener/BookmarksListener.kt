package org.metailurini.jetmeil.plugins.listener

import com.intellij.ide.bookmark.Bookmark
import com.intellij.ide.bookmark.BookmarkGroup
import com.intellij.ide.bookmark.BookmarksListener
import com.intellij.openapi.project.ProjectManager
import org.metailurini.jetmeil.ActionQueries
import org.metailurini.jetmeil.Project
import org.metailurini.jetmeil.adapter.GitCommander
import org.metailurini.jetmeil.common.Utils
import org.metailurini.jetmeil.loadProject

open class BookmarksListener(
    private var db: ActionQueries,
    private var gitter: GitCommander,
    private var initProject: Project?
) :
    BookmarksListener {
    private val project: Project

    init {
        if (initProject == null) {
            ProjectManager.getInstance().openProjects[0].basePath?.let {
                initProject = loadProject(db, gitter, it)
            }
        }
        project = initProject as Project
    }

    override fun groupRenamed(group: BookmarkGroup) {
        val bookmarks = group.getBookmarks()
        if (bookmarks.isEmpty()) {
            return
        }

        val bookmark = bookmarks[0]
        val newGroupName = group.name
        val projectID = project.project_id
        val (fileURL, fileLine) = extractFileAndLineNum(bookmark)

        db.UpdateGroupName(newGroupName, projectID, fileURL, fileLine)
    }

    override fun groupRemoved(group: BookmarkGroup) {
        db.RemoveByGroupName(group.name)
    }

    override fun bookmarkAdded(group: BookmarkGroup, bookmark: Bookmark) {
        val projectID = project.project_id
        val groupName = group.name
        val description = group.getDescription(bookmark)
        val commitID = getCurrentCommitID(project.project_path)
        val (fileURL, fileLine, isGroupBookmarkName) = extractFileAndLineNum(bookmark)
        if (isGroupBookmarkName) {
            return
        }

        db.UpsertBookmarkWithoutCommitID(projectID, groupName, description, fileURL, fileLine, commitID)
    }

    override fun bookmarkChanged(group: BookmarkGroup, bookmark: Bookmark) {
        val projectID = project.project_id
        val groupName = group.name
        val description = group.getDescription(bookmark)
        val commitID = getCurrentCommitID(project.project_path)
        val (fileURL, fileLine, isGroupBookmarkName) = extractFileAndLineNum(bookmark)
        if (isGroupBookmarkName) {
            return
        }

        db.UpsertBookmark(projectID, groupName, description, fileURL, fileLine, commitID)
    }

    override fun bookmarkRemoved(group: BookmarkGroup, bookmark: Bookmark) {
        val projectID = project.project_id
        val (fileURL, fileLine, isGroupBookmarkName) = extractFileAndLineNum(bookmark)
        if (isGroupBookmarkName) {
            return
        }

        db.DeletedBookmark(projectID, fileURL, fileLine)
    }
}

private operator fun Any.component0() {}

internal fun extractFileAndLineNum(bookmark: Bookmark): Triple<String, Long, Boolean> {
    var fileURL: String?
    val fileLine: Long?
    var isGroupBookmarkName = false

    fileURL = bookmark.attributes["url"]
    if (fileURL == null) {
        fileURL = ""
    }

    val line = bookmark.attributes["line"]
    fileLine = if (line == null) {
        isGroupBookmarkName = true
        0
    } else {
        try {
            line.toLong().plus(1)
        } catch (e: NumberFormatException) {
            isGroupBookmarkName = true
            0
        }
    }

    return Triple(fileURL, fileLine, isGroupBookmarkName)
}

internal fun getCurrentCommitID(basePath: String): String {
    return Utils.run(arrayOf("sh", "-c", "cd '${basePath}' && git rev-parse --short HEAD"))
}
