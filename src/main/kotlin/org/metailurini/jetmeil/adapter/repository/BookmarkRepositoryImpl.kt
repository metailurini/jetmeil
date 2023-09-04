package org.metailurini.jetmeil.adapter.repository

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransactionWithReturn
import com.squareup.sqldelight.TransactionWithoutReturn
import org.metailurini.jetmeil.Bookmark
import org.metailurini.jetmeil.BookmarkQueries
import org.metailurini.jetmeil.Project

class BookmarkRepositoryImpl(private var origin: BookmarkQueries) : BookmarkRepository {
    override fun transaction(noEnclosing: Boolean, body: TransactionWithoutReturn.() -> Unit) {
        origin.transaction(noEnclosing, body)
    }

    override fun <R> transactionWithResult(noEnclosing: Boolean, bodyWithReturn: TransactionWithReturn<R>.() -> R): R {
        return origin.transactionWithResult(noEnclosing, bodyWithReturn)
    }

    override fun <T : Any> GetProjectByPath(
        project_path: String,
        mapper: (projectID: Long, project_path: String, github_link: String?) -> T
    ): Query<T> {
        return origin.GetProjectByPath(project_path, mapper)
    }

    override fun GetProjectByPath(project_path: String): Query<Project> {
        return origin.GetProjectByPath(project_path)
    }

    override fun <T : Any> GetProject(mapper: (project_id: Long, project_path: String, github_link: String?) -> T): Query<T> {
        return origin.GetProject(mapper)
    }

    override fun GetProject(): Query<Project> {
        return origin.GetProject()
    }

    override fun <T : Any> GetBookmarkByKey(
        project_id: Long,
        file_path: String,
        line_number: Long,
        mapper: (project_id: Long, group_name: String, description: String?, file_path: String, line_number: Long, commit_id: String) -> T
    ): Query<T> {
        return origin.GetBookmarkByKey(project_id, file_path, line_number, mapper)
    }

    override fun GetBookmarkByKey(project_id: Long, file_path: String, line_number: Long): Query<Bookmark> {
        return origin.GetBookmarkByKey(project_id, file_path, line_number)
    }

    override fun UpdateGroupName(group_name: String, project_id: Long, file_path: String, line_number: Long) {
        origin.UpdateGroupName(group_name, project_id, file_path, line_number)
    }

    override fun RemoveByGroupName(project_id: Long, group_name: String) {
        origin.RemoveByGroupName(project_id, group_name)
    }

    override fun UpsertBookmark(
        project_id: Long,
        group_name: String,
        description: String?,
        file_path: String,
        line_number: Long,
        commit_id: String
    ) {
        origin.UpsertBookmark(project_id, group_name, description, file_path, line_number, commit_id)
    }

    override fun UpsertBookmarkWithoutCommitID(
        project_id: Long,
        group_name: String,
        description: String?,
        file_path: String,
        line_number: Long,
        commit_id: String
    ) {
        origin.UpsertBookmarkWithoutCommitID(project_id, group_name, description, file_path, line_number, commit_id)
    }

    override fun UpsertProject(project_id: Long?, project_path: String, github_link: String?) {
        origin.UpsertProject(project_id, project_path, github_link)
    }

    override fun DeletedBookmark(project_id: Long, file_path: String, line_number: Long) {
        origin.DeletedBookmark(project_id, file_path, line_number)
    }

    override fun getProjectsByPath(projectPath: String): List<Project> {
        return origin.GetProjectByPath(projectPath).executeAsList()
    }

    override fun getProjects(): List<Project> {
        return origin.GetProject().executeAsList()
    }
}