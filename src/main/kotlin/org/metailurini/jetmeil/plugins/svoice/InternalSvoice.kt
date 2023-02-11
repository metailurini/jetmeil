package org.metailurini.jetmeil.plugins.svoice

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.intellij.util.io.DataOutputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

internal class InternalSvoice {
    private fun requestBody(text: String): JsonObject {
        val jsonObject = JsonParser.parseString(
            """
                {
                    "audioFormat": "mp3",
                    "paragraphChunks": [],
                    "voiceParams": {
                        "name": "Matthew",
                        "engine": "neural",
                        "languageCode": "en-US"
                    }
                }
            """.trimIndent()
        ).asJsonObject
        val jsonElement = jsonObject["paragraphChunks"].asJsonArray
        jsonElement.add(text)
        return jsonObject
    }

    fun downloadVoiceAudio(text: String, path: Path) {
        val textData = this.requestBody(text).toString()

        with(
            URL("https://audio.api.speechify.dev/generateAudioFiles").openConnection() as HttpURLConnection
        ) {
            requestMethod = "POST"
            setRequestProperty("authority", "audio.api.speechify.dev")
            setRequestProperty("accept", "*/*")
            setRequestProperty("accept-base64", "true")
            setRequestProperty("accept-language", "en-US,en;q=0.9,vi;q=0.8")
            setRequestProperty("content-type", "application/json; charset=UTF-8")
            setRequestProperty("dnt", "1")
            setRequestProperty("x-speechify-client", "API")
            setRequestProperty("Content-Type", "application/json")
            doOutput = true

            DataOutputStream(outputStream).apply {
                writeBytes(textData)
                flush()
                close()
            }

            BufferedReader(InputStreamReader(inputStream)).use { bufferedReader ->
                val response = StringBuffer()
                var inputLine = bufferedReader.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = bufferedReader.readLine()
                }
                val parseString = JsonParser.parseString(response.toString()).asJsonObject
                val audioStream = parseString["audioStream"].asString
                val binaryData = Base64.getDecoder().decode(audioStream)

                Files.write(path, binaryData)
            }
        }
    }

    fun playAudio(path: Path) {
        println("run command $path")
        val processBuilder = ProcessBuilder(
            "/usr/bin/cvlc",
            "--play-and-exit",
            path.toAbsolutePath().toString(),
        )
        val process = processBuilder.start()
        val inputStreamReader = InputStreamReader(process.inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            println(line)
        }
    }
}