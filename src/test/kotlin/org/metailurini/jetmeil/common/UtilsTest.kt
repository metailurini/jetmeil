package org.metailurini.jetmeil.common

import junit.framework.TestCase
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class UtilsTest : TestCase() {
    private val testCommand = "echo Hello, World!"

    fun testRun() {
        val commandArgs = testCommand.split(" ").toTypedArray()
        val originalOut = System.out
        val outputStream = ByteArrayOutputStream()

        System.setOut(PrintStream(outputStream))
        val result = Utils.run(commandArgs)
        System.setOut(originalOut)

        val expectedOutput = "Hello, World!\n"
        assertEquals(expectedOutput, outputStream.toString())
        assertEquals(expectedOutput.trim(), result.trim())
    }
}