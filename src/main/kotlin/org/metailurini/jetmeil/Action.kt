package org.metailurini.jetmeil

import com.intellij.openapi.actionSystem.AnActionEvent

interface Plugin {
    fun actionPerformed(event: AnActionEvent)
}