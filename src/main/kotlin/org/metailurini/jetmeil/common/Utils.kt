package org.metailurini.jetmeil.common

import java.io.BufferedReader
import java.io.InputStreamReader

class Utils {
    companion object {
        @JvmStatic
        fun run(params: Array<String>): String {
            val processBuilder = ProcessBuilder(params.toMutableList())
            val process = processBuilder.start()
            val inputStreamReader = InputStreamReader(process.inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var line: String?
            val lines = mutableListOf<String>()
            while (bufferedReader.readLine().also { line = it } != null) {
                line?.let { lines.add(it) }
                println(line)
            }
            return lines.joinToString(" ")
        }
    }
}