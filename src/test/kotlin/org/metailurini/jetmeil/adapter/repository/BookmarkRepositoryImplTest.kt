package org.metailurini.jetmeil.adapter.repository

import junit.framework.TestCase
import org.metailurini.jetmeil.adapter.DatabaseManager
import java.io.File

class BookmarkRepositoryImplTest : TestCase() {
    private lateinit var db: DatabaseManager
    private lateinit var bookmarkRepo: BookmarkRepository
    private val tmpDBPath = "/tmp/tmp.db"

    override fun setUp() {
        db = DatabaseManager("jdbc:sqlite:$tmpDBPath")
        bookmarkRepo = BookmarkRepositoryImpl(db.getBookmarkQueries())
    }

    override fun tearDown() {
        db.driver.close()
        File(tmpDBPath).delete()
    }

    fun testProject() {
        // test insert project
        bookmarkRepo.UpsertProject(1, "test1", "github.com/test")
        bookmarkRepo.UpsertProject(2, "test2", "github.com/test")

        // test get project by path
        assert(bookmarkRepo.getProjectsByPath("test").isEmpty())
        assert(bookmarkRepo.getProjectsByPath("test1").size == 1)

        // test get all projects
        assert(bookmarkRepo.getProjects().size == 2)
    }

    fun testBookmark() {
        bookmarkRepo.UpsertProject(1, "test1", "github.com/test")

        // test insert bookmark
        bookmarkRepo.UpsertBookmark(1, "group_name", "description", "file_path", 1, "commit_id")
        assert(
            bookmarkRepo
                .GetBookmarkByKey(1, "file_path", 1)
                .executeAsList()
                .size == 1
        )

        // test update bookmark without commit id
        bookmarkRepo.UpsertBookmarkWithoutCommitID(
            1,
            "group_name.edited",
            "description.edited",
            "file_path",
            1,
            "commit_id.edited",
        )
        var bookmarks = bookmarkRepo
            .GetBookmarkByKey(1, "file_path", 1)
            .executeAsList()
        assert(bookmarks.size == 1)
        assert(bookmarks[0].group_name == "group_name.edited")
        assert(bookmarks[0].description == "description.edited")
        assert(bookmarks[0].commit_id == "commit_id")

        // test delete bookmark
        bookmarkRepo.DeletedBookmark(1, "file_path", 1)
        bookmarks = bookmarkRepo
            .GetBookmarkByKey(1, "file_path", 1)
            .executeAsList()
        assert(bookmarks.isEmpty())
    }

    fun testGroupBookmark() {
        bookmarkRepo.UpsertProject(1, "test1", "github.com/test")
        bookmarkRepo.UpsertProject(2, "test1", "github.com/test")

        bookmarkRepo.UpsertBookmark(1, "group_name", "description", "file_path", 1, "commit_id")
        bookmarkRepo.UpsertBookmark(1, "group_name", "description", "file_path", 2, "commit_id")
        bookmarkRepo.UpsertBookmark(2, "group_name", "description", "file_path", 1, "commit_id")

        // test update group name
        bookmarkRepo.UpdateGroupName("group_name.edited", 1, "file_path", 1)
        bookmarkRepo.GetBookmarkByKey(1, "file_path", 1).executeAsList().forEach {
            assert(it.group_name == "group_name.edited")
        }
        bookmarkRepo.GetBookmarkByKey(2, "file_path", 1).executeAsList().forEach {
            assert(it.group_name == "group_name")
        }

        // test delete group
        bookmarkRepo.RemoveByGroupName(1, "group_name.edited")
        assert(bookmarkRepo.GetBookmarkByKey(1, "file_path", 1).executeAsList().isEmpty())
        assert(bookmarkRepo.GetBookmarkByKey(2, "file_path", 1).executeAsList().size == 1)
    }
}
