package org.metailurini.jetmeil.adapter

import org.metailurini.jetmeil.common.Utils

class GitCommanderImpl : GitCommander {
    override fun getRemoteURL(basePath: String): String? {
        val gitRemoteRaw = Utils.run(arrayOf("sh", "-c", "cd '$basePath' && git remote -v"))
        return extractGitRemoteURL(gitRemoteRaw)
    }

    override fun getCurrentCommitID(basePath: String): String {
        return Utils.run(arrayOf("sh", "-c", "cd '${basePath}' && git rev-parse --short HEAD"))
    }
}

private operator fun Any.component0() {}

internal fun extractGitRemoteURL(input: String): String? {
    val regex = Regex("([^/@ ]+/[^/ ]*/[^/ ]+) .[^()]*.")
    val matchResult = regex.find(input)
    return matchResult?.groups?.get(1)?.value
}
