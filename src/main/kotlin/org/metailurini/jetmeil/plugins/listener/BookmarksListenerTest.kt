package org.metailurini.jetmeil.plugins.listener

import com.intellij.ide.bookmark.Bookmark
import com.intellij.ide.bookmark.BookmarkGroup
import com.intellij.ide.bookmark.BookmarkProvider
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import groovy.lang.Tuple4
import junit.framework.TestCase
import org.metailurini.jetmeil.ActionQueries
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever
import org.metailurini.jetmeil.Project as JetmeilProject

private class MockBookmarkProvider : BookmarkProvider {
    override fun compare(p0: Bookmark?, p1: Bookmark?): Int {
        TODO("Not yet implemented")
    }

    override fun getWeight(): Int {
        TODO("Not yet implemented")
    }

    override fun getProject(): Project {
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

    private fun setupMock(): Pair<ActionQueries, JetmeilProject> {
        val db = mock<ActionQueries>()
        val project = JetmeilProject(1, ".", "github.com")
        return Pair(db, project)
    }

    fun testGroupRenamed() {
        val (db, project) = setupMock()
        val group = mock<BookmarkGroup>()
        val bookMark = mock<Bookmark>()

        whenever(group.name).thenReturn("group")
        whenever(group.getBookmarks()).thenReturn(listOf(bookMark))
        whenever(bookMark.attributes).thenReturn(mapOf("url" to "fn", "line" to "42"))

        BookmarksListener(db, project).groupRenamed(group)

        verify(db).UpdateGroupName(group.name, project.project_id, "fn", 43)
    }

    fun testGroupRemoved() {
        val (db, project) = setupMock()
        val group = mock<BookmarkGroup>()

        whenever(group.name).thenReturn("group")

        BookmarksListener(db, project).groupRemoved(group)

        verify(db).RemoveByGroupName(group.name)
    }


    data class TestCase(
        val testName: String,
        val setup: () -> Pair<
                Tuple4<ActionQueries, JetmeilProject, BookmarkGroup, Bookmark>,
                    () -> Unit
                >,
    )

    fun testBookmarkAdded() {
        val testCases = mutableListOf(
            TestCase(
                "Test Case 1: add bookmark",
            ) {
                val (db, project) = setupMock()
                val group = mock<BookmarkGroup>()
                val bookMark = mock<Bookmark>()

                whenever(group.name).thenReturn("group")
                whenever(group.getDescription(bookMark)).thenReturn("des")
                val commitID = getCurrentCommitID(project.project_path)
                whenever(bookMark.attributes).thenReturn(mapOf("url" to "fn", "line" to "10"))
                return@TestCase Pair(
                    Tuple4(db, project, group, bookMark)
                ) {
                    verify(db).UpsertBookmarkWithoutCommitID(
                        project.project_id,
                        group.name,
                        group.getDescription(bookMark),
                        "fn",
                        11,
                        commitID
                    )
                }
            },
            TestCase(
                "Test Case 2: add group bookmark name",
            ) {
                val (db, project) = setupMock()
                val group = mock<BookmarkGroup>()
                val bookMark = mock<Bookmark>()

                whenever(group.name).thenReturn("group")
                whenever(group.getDescription(bookMark)).thenReturn("des")
                return@TestCase Pair(
                    Tuple4(db, project, group, bookMark)
                ) {
                    verifyZeroInteractions(db)
                }
            }
        )

        for (testCase in testCases) {
            val testName = testCase.testName
            val (mock, verify) = testCase.setup()
            println("Running test case: $testName")

            BookmarksListener(mock.v1, mock.v2).bookmarkAdded(mock.v3, mock.v4)

            verify()
        }
    }

    fun testBookmarkChanged() {
        val testCases = mutableListOf(
            TestCase(
                "Test Case 1: update bookmark",
            ) {
                val (db, project) = setupMock()
                val group = mock<BookmarkGroup>()
                val bookMark = mock<Bookmark>()

                whenever(group.name).thenReturn("group")
                whenever(group.getDescription(bookMark)).thenReturn("des")
                val commitID = getCurrentCommitID(project.project_path)
                whenever(bookMark.attributes).thenReturn(mapOf("url" to "fn", "line" to "50"))
                return@TestCase Pair(
                    Tuple4(db, project, group, bookMark)
                ) {
                    verify(db).UpsertBookmark(
                        project.project_id,
                        group.name,
                        group.getDescription(bookMark),
                        "fn",
                        51,
                        commitID
                    )
                }
            },
            TestCase(
                "Test Case 2: update group bookmark name",
            ) {
                val (db, project) = setupMock()
                val group = mock<BookmarkGroup>()
                val bookMark = mock<Bookmark>()

                whenever(group.name).thenReturn("group")
                whenever(group.getDescription(bookMark)).thenReturn("des")
                return@TestCase Pair(
                    Tuple4(db, project, group, bookMark)
                ) {
                    verifyZeroInteractions(db)
                }
            }
        )

        for (testCase in testCases) {
            val testName = testCase.testName
            val (mock, verify) = testCase.setup()
            println("Running test case: $testName")

            BookmarksListener(mock.v1, mock.v2).bookmarkChanged(mock.v3, mock.v4)

            verify()
        }
    }

    fun testBookmarkRemoved() {
        val testCases = mutableListOf(
            TestCase(
                "Test Case 1: remove bookmark",
            ) {
                val (db, project) = setupMock()
                val bookMark = mock<Bookmark>()

                whenever(bookMark.attributes).thenReturn(mapOf("url" to "fn", "line" to "50"))
                return@TestCase Pair(
                    Tuple4(db, project, mock(), bookMark)
                ) {
                    verify(db).DeletedBookmark(project.project_id, "fn", 51)
                }
            },
            TestCase(
                "Test Case 2: remove group bookmark",
            ) {
                val (db, project) = setupMock()
                val bookMark = mock<Bookmark>()

                whenever(bookMark.attributes).thenReturn(mapOf("url" to "fn"))
                return@TestCase Pair(
                    Tuple4(db, project, mock(), bookMark)
                ) {
                    verifyZeroInteractions(db)
                }
            },
        )

        for (testCase in testCases) {
            val testName = testCase.testName
            val (mock, verify) = testCase.setup()
            println("Running test case: $testName")

            BookmarksListener(mock.v1, mock.v2).bookmarkRemoved(mock.v3, mock.v4)

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

    fun testGetCurrentCommitID() {
        val currentCommitID = getCurrentCommitID(".")
        assertTrue(currentCommitID.isNotEmpty())
    }
}
