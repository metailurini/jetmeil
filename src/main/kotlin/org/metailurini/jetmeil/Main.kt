package org.metailurini.jetmeil

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import org.metailurini.jetmeil.builtin.Plugin
import org.metailurini.jetmeil.plugins.svoice.Svoice

class Main {
    companion object {
        @OptIn(DelicateCoroutinesApi::class)
        var svoice: Plugin = Svoice.getInstance()
    }

    class SvoiceAction : AnAction() {
        override fun actionPerformed(event: AnActionEvent) {
            try {
                svoice.actionPerformed(event)
            } catch (e: Exception) {
                println("= svoice.actionPerformed $e")
            }
        }
    }
}