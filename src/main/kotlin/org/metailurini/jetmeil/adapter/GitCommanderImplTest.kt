package org.metailurini.jetmeil.adapter

import junit.framework.TestCase

class GitCommanderImplTest : TestCase() {

    fun testGetRemoteURL() {
        val currentCommitID = GitCommanderImpl().getRemoteURL(".")
        assertNotNull(currentCommitID)
        if (currentCommitID != null) {
            assertTrue(currentCommitID.isNotEmpty())
        }
    }

    fun testGetCurrentCommitID() {
        val currentCommitID = GitCommanderImpl().getCurrentCommitID(".")
        assertTrue(currentCommitID.isNotEmpty())
    }

    fun testExtractGitRemoteURL() {
        val cases = listOf(
            Pair("fatal: not a git repository (or any of the parent directories): .git", null),
            Pair(
                "origin https://xxx:xxx@github.com/metailurini/monotrematum (fetch)",
                "github.com/metailurini/monotrematum"
            ),
            Pair(
                "origin https://xxx:xxx@github.com/metailurini/monotrematum (push)",
                "github.com/metailurini/monotrematum"
            ),
            Pair(
                "origin https://xxx:xxx@github.com/metailurini/monotrematum (push) origin https://xxx:xxx@github.com/none/monotrematum (push)",
                "github.com/metailurini/monotrematum"
            ),
            Pair(
                "",
                null
            ),
        )
        cases.forEach { (input: String, expectedOutput: String?) ->
            val actualOutput = extractGitRemoteURL(input)
            assertEquals(expectedOutput, actualOutput)
        }
    }
}