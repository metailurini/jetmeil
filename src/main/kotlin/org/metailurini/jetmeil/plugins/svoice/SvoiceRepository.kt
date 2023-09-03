package org.metailurini.jetmeil.plugins.svoice

import java.nio.file.Path

interface SvoiceRepository {
    fun downloadVoiceAudio(text: String, path: Path)
    fun playAudio(audioPath: Path)
}