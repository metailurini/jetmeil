package org.metailurini.jetmeil.plugins.svoice

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.util.TextRange
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.metailurini.jetmeil.Plugin
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest

@DelicateCoroutinesApi
class Svoice(private var svoiceRepo: SvoiceRepository) : Plugin {
    private lateinit var tmpDir: Path
    private var now = System.currentTimeMillis()

    override fun actionPerformed(event: AnActionEvent) {
        val selectedText = this.getText(event) ?: return

        println("@ now $now")

        val tmpDir = this.getTmpDir()
        val soundName = Paths.get(tmpDir.toString(), this.hashString(selectedText))

        GlobalScope.launch {
            val listFileName = tmpDir.toFile().listFiles()!!.map { f -> f.absolutePath }
            if (listFileName.indexOf(soundName.toAbsolutePath().toString()) == -1) {
                svoiceRepo.downloadVoiceAudio(selectedText, soundName)
            }

            svoiceRepo.playAudio(soundName)
        }
    }

    private fun getTmpDir(): Path {
        if (!this::tmpDir.isInitialized) {
            this.tmpDir = Files.createTempDirectory("tmp")
        }
        return this.tmpDir
    }

    private fun hashString(input: String, algorithm: String = "SHA-256"): String {
        val messageDigest = MessageDigest.getInstance(algorithm)
        val hashedBytes = messageDigest.digest(input.toByteArray())

        return hashedBytes.joinToString(separator = "") {
            String.format("%02x", it)
        }
    }

    private fun getText(event: AnActionEvent): String? {
        val editor = event.getData(CommonDataKeys.EDITOR)
        var selectedText: String? = null

        if (editor != null) {
            selectedText = editor.selectionModel.selectedText
            if (selectedText == null) {
                val primaryCaret = editor.caretModel.primaryCaret
                val selectionStart = editor.document.getLineStartOffset(primaryCaret.logicalPosition.line)
                val selectionEnd = editor.document.getLineEndOffset(primaryCaret.logicalPosition.line)
                selectedText = editor.document.getText(TextRange(selectionStart, selectionEnd))
            }
        }

        return selectedText
    }
}
