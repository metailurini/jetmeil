package org.metailurini.jetmeil.plugins.bookmark

import com.intellij.ide.bookmark.Bookmark
import com.intellij.ide.bookmark.BookmarkGroup
import com.intellij.ide.bookmark.BookmarksListener
import org.metailurini.jetmeil.Project
import org.metailurini.jetmeil.adapter.GitCommander
import org.metailurini.jetmeil.adapter.repository.BookmarkRepository


open class BookmarksListener(
    private var bookmarkRepo: BookmarkRepository,
    private var gitter: GitCommander,
    private var projects: List<Project>
) :
    BookmarksListener {

    companion object {
        const val PROJECT_NOT_FOUND = "project not found"
    }

    override fun groupRenamed(group: BookmarkGroup) {
        val bookmarks = group.getBookmarks()
        if (bookmarks.isEmpty()) {
            return
        }

        val bookmark = bookmarks[0]
        val newGroupName = group.name
        val projectID = getProjectByBookmark(bookmark).project_id
        val (fileURL, fileLine) = extractFileAndLineNum(bookmark)

        bookmarkRepo.UpdateGroupName(newGroupName, projectID, fileURL, fileLine)
    }

    override fun groupRemoved(group: BookmarkGroup) {
        bookmarkRepo.RemoveByGroupName(group.name)
    }

    override fun bookmarkAdded(group: BookmarkGroup, bookmark: Bookmark) {
        val project = getProjectByBookmark(bookmark)
        val projectID = project.project_id
        val groupName = group.name
        val description = group.getDescription(bookmark)
        val commitID = gitter.getCurrentCommitID(project.project_path)
        val (fileURL, fileLine, isGroupBookmarkName) = extractFileAndLineNum(bookmark)
        if (isGroupBookmarkName) {
            return
        }

        bookmarkRepo.UpsertBookmarkWithoutCommitID(projectID, groupName, description, fileURL, fileLine, commitID)
    }

    override fun bookmarkChanged(group: BookmarkGroup, bookmark: Bookmark) {
        val project = getProjectByBookmark(bookmark)
        val projectID = project.project_id
        val groupName = group.name
        val description = group.getDescription(bookmark)
        val commitID = gitter.getCurrentCommitID(project.project_path)
        val (fileURL, fileLine, isGroupBookmarkName) = extractFileAndLineNum(bookmark)
        if (isGroupBookmarkName) {
            return
        }

        bookmarkRepo.UpsertBookmark(projectID, groupName, description, fileURL, fileLine, commitID)
    }

    override fun bookmarkRemoved(group: BookmarkGroup, bookmark: Bookmark) {
        val projectID = getProjectByBookmark(bookmark).project_id
        val (fileURL, fileLine, isGroupBookmarkName) = extractFileAndLineNum(bookmark)
        if (isGroupBookmarkName) {
            return
        }

        bookmarkRepo.DeletedBookmark(projectID, fileURL, fileLine)
    }

    internal fun getProjectByBookmark(bookmark: Bookmark): Project {
        val filteredProjects = projects.filter { it.project_path == bookmark.provider.project.basePath }
        if (filteredProjects.isEmpty()) {
            throw Exception(PROJECT_NOT_FOUND)
        }
        return filteredProjects[0]
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