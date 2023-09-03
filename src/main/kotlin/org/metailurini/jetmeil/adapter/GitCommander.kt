package org.metailurini.jetmeil.adapter

interface GitCommander {
    fun getRemoteURL(basePath: String): String?
    fun getCurrentCommitID(basePath: String): String
}