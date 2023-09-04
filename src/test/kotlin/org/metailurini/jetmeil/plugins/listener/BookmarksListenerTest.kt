package org.metailurini.jetmeil.plugins.listener

import com.intellij.ide.bookmark.Bookmark
import com.intellij.ide.bookmark.BookmarkGroup
import com.intellij.ide.bookmark.BookmarkProvider
import com.intellij.ide.util.treeView.AbstractTreeNode
import groovy.lang.Tuple5
import junit.framework.TestCase
import org.metailurini.jetmeil.Project
import org.metailurini.jetmeil.adapter.GitCommander
import org.metailurini.jetmeil.adapter.repository.BookmarkRepository
import org.metailurini.jetmeil.plugins.listener.BookmarksListener.Companion.PROJECT_NOT_FOUND
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

private class MockBookmarkProvider : BookmarkProvider {
    override fun compare(p0: Bookmark?, p1: Bookmark?): Int {
        TODO("Not yet implemented")
    }

    override fun getWeight(): Int {
        TODO("Not yet implemented")
    }

    override fun getProject(): com.intellij.openapi.project.Project {
        TODO("Not yet implemented")
    }

    override fun createBookmark(map: MutableMap<String, String>): Bookmark? {
        TODO("Not yet implemented")
    }

    override fun createBookmark(context: Any?): Bookmark? {
        TODO("Not yet implemented")
    }
}


private class MockBookmark(override val attributes: Map<String, String>, override val provider: BookmarkProvider) :
    Bookmark {
    override fun canNavigate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canNavigateToSource(): Boolean {
        TODO("Not yet implemented")
    }

    override fun createNode(): AbstractTreeNode<*> {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        TODO("Not yet implemented")
    }

    override fun navigate(requestFocus: Boolean) {
        TODO("Not yet implemented")
    }
}


class BookmarksListenerTest : TestCase() {

    companion object {
        const val DEFAULT_PROJECT_PATH = "."
        const val DEFAULT_COMMIT_ID = "abc"
    }

    data class TestCase(
        val testName: String,
        val setup: () -> Pair<
                Tuple5<BookmarkRepository, GitCommander, List<Project>, BookmarkGroup, Bookmark>,
                    () -> Unit
                >,
    )

    private fun setupMock(): Pair<BookmarkRepository, List<Project>> {
        val db = mock<BookmarkRepository>()
        val project = Project(1, DEFAULT_PROJECT_PATH, "github.com")
        return Pair(db, listOf(project))
    }

    private fun setupMockForGettingProject(): Triple<BookmarkGroup, Bookmark, com.intellij.openapi.project.Project> {
        val group = mock<BookmarkGroup>()
        val bookMark = mock<Bookmark>()
        val bookmarkProvider = mock<BookmarkProvider>()
        val project = mock<com.intellij.openapi.project.Project>()

        whenever(group.getBookmarks()).thenReturn(listOf(bookMark))
        whenever(bookMark.provider).thenReturn(bookmarkProvider)
        whenever(bookmarkProvider.project).thenReturn(project)
        whenever(project.basePath).thenReturn(DEFAULT_PROJECT_PATH)

        return Triple(group, bookMark, project)
    }

    fun testGroupRenamed() {
        val (db, projects) = setupMock()
        val (group, bookMark) = setupMockForGettingProject()

        whenever(group.name).thenReturn("group")
        whenever(bookMark.attributes).thenReturn(mapOf("url" to "fn", "line" to "42"))

        BookmarksListener(db, mock(), projects).groupRenamed(group)

        verify(db).UpdateGroupName(group.name, 1, "fn", 43)
    }

    fun testGroupRemoved() {
        val (db, projects) = setupMock()
        val group = mock<BookmarkGroup>()

        whenever(group.name).thenReturn("group")

        BookmarksListener(db, mock(), projects).groupRemoved(group)

        verify(db).RemoveByGroupName(group.name)
    }

    fun testBookmarkAdded() {
        val testCases = mutableListOf(
            TestCase(
                "Test Case 1: add bookmark",
            ) {
                val (db, projects) = setupMock()
                val (group, bookMark, project) = setupMockForGettingProject()
                val gitter = mock<GitCommander>()

                whenever(group.name).thenReturn("group")
                whenever(group.getDescription(bookMark)).thenReturn("des")
                whenever(bookMark.attributes).thenReturn(mapOf("url" to "fn", "line" to "10"))
                whenever(gitter.getCurrentCommitID(project.basePath!!)).thenReturn(DEFAULT_COMMIT_ID)

                return@TestCase Pair(
                    Tuple5(db, gitter, projects, group, bookMark)
                ) {
                    verify(db).UpsertBookmarkWithoutCommitID(
                        1,
                        group.name,
                        group.getDescription(bookMark),
                        "fn",
                        11,
                        DEFAULT_COMMIT_ID
                    )
                }
            },
            TestCase(
                "Test Case 2: add group bookmark name",
            ) {
                val (db, projects) = setupMock()
                val (group, bookMark) = setupMockForGettingProject()

                whenever(group.name).thenReturn("group")
                whenever(group.getDescription(bookMark)).thenReturn("des")

                return@TestCase Pair(
                    Tuple5(db, mock(), projects, group, bookMark)
                ) {
                    verifyNoInteractions(db)
                }
            }
        )

        for (testCase in testCases) {
            val testName = testCase.testName
            val (mock, verify) = testCase.setup()
            println("Running test case: $testName")

            BookmarksListener(mock.v1, mock.v2, mock.v3).bookmarkAdded(mock.v4, mock.v5)

            verify()
        }
    }

    fun testBookmarkChanged() {
        val testCases = mutableListOf(
            TestCase(
                "Test Case 1: update bookmark",
            ) {
                val (db, projects) = setupMock()
                val (group, bookMark, project) = setupMockForGettingProject()
                val gitter = mock<GitCommander>()

                whenever(group.name).thenReturn("group")
                whenever(group.getDescription(bookMark)).thenReturn("des")
                whenever(bookMark.attributes).thenReturn(mapOf("url" to "fn", "line" to "50"))
                whenever(gitter.getCurrentCommitID(project.basePath!!)).thenReturn(DEFAULT_COMMIT_ID)

                return@TestCase Pair(
                    Tuple5(db, gitter, projects, group, bookMark)
                ) {
                    verify(db).UpsertBookmark(
                        1,
                        group.name,
                        group.getDescription(bookMark),
                        "fn",
                        51,
                        DEFAULT_COMMIT_ID
                    )
                }
            },
            TestCase(
                "Test Case 2: update group bookmark name",
            ) {
                val (db, projects) = setupMock()
                val (group, bookMark) = setupMockForGettingProject()

                whenever(group.name).thenReturn("group")
                whenever(group.getDescription(bookMark)).thenReturn("des")

                return@TestCase Pair(
                    Tuple5(db, mock(), projects, group, bookMark)
                ) {
                    verifyNoInteractions(db)
                }
            }
        )

        for (testCase in testCases) {
            val testName = testCase.testName
            val (mock, verify) = testCase.setup()
            println("Running test case: $testName")

            BookmarksListener(mock.v1, mock.v2, mock.v3).bookmarkChanged(mock.v4, mock.v5)

            verify()
        }
    }

    fun testBookmarkRemoved() {
        val testCases = mutableListOf(
            TestCase(
                "Test Case 1: remove bookmark",
            ) {
                val (db, projects) = setupMock()
                val (_, bookMark) = setupMockForGettingProject()

                whenever(bookMark.attributes).thenReturn(mapOf("url" to "fn", "line" to "50"))

                return@TestCase Pair(
                    Tuple5(db, mock(), projects, mock(), bookMark)
                ) {
                    verify(db).DeletedBookmark(1, "fn", 51)
                }
            },
            TestCase(
                "Test Case 2: remove group bookmark",
            ) {
                val (db, projects) = setupMock()
                val (_, bookMark) = setupMockForGettingProject()

                whenever(bookMark.attributes).thenReturn(mapOf("url" to "fn"))

                return@TestCase Pair(
                    Tuple5(db, mock(), projects, mock(), bookMark)
                ) {
                    verifyNoInteractions(db)
                }
            },
        )

        for (testCase in testCases) {
            val testName = testCase.testName
            val (mock, verify) = testCase.setup()
            println("Running test case: $testName")

            BookmarksListener(mock.v1, mock.v2, mock.v3).bookmarkRemoved(mock.v4, mock.v5)

            verify()
        }
    }

    fun testExtractFileAndLineNum() {
        val cases = listOf(
            Pair(MockBookmark(mapOf("url" to "fn", "line" to "42"), MockBookmarkProvider()), Triple("fn", 43L, false)),
            Pair(MockBookmark(mapOf("url" to "fn", "line" to "0"), MockBookmarkProvider()), Triple("fn", 1L, false)),
            Pair(MockBookmark(mapOf("url" to "fn"), MockBookmarkProvider()), Triple("fn", 0L, true)),
            Pair(MockBookmark(mapOf(), MockBookmarkProvider()), Triple("", 0L, true)),
        )

        cases.forEach { (inputBookmark: Bookmark, expectedOutput: Triple<String, Long, Boolean>) ->
            val actualOutput = extractFileAndLineNum(inputBookmark)
            assertEquals(expectedOutput, actualOutput)
        }
    }

    fun testGetProjectByBookmark() {
        // test case: project not found
        val bookMark = mock<Bookmark>()
        val bookmarkProvider = mock<BookmarkProvider>()
        val project = mock<com.intellij.openapi.project.Project>()

        whenever(bookMark.provider).thenReturn(bookmarkProvider)
        whenever(bookmarkProvider.project).thenReturn(project)
        whenever(project.basePath).thenReturn(DEFAULT_PROJECT_PATH)

        try {
            BookmarksListener(mock(), mock(), listOf(Project(1, "path", "github.com")))
                .getProjectByBookmark(bookMark)
            fail()
        } catch (e: Exception) {
            assertEquals(PROJECT_NOT_FOUND, e.message)
        }
    }
}