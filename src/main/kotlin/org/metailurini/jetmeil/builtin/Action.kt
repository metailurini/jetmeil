package org.metailurini.jetmeil.builtin

import com.intellij.openapi.actionSystem.AnActionEvent

interface Plugin {
    fun actionPerformed(event: AnActionEvent)
}