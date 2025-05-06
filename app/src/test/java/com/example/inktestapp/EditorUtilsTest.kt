package com.example.inktestapp

import com.example.inktestapp.utils.EditorUtils
import org.junit.Assert
import org.junit.Test

class EditorUtilsTest {

    val message = "Writing unit tests is never a waste of time. It can be difficult, but it will always be worth the effort. If not for unit tests, developers would have a low confidence level in business logic as well as having many apps that are buggy and not ready for projection. While it may not be the funnest this to write, it is definitely the most important."

    @Test
    fun testExtractKeySentences() {
        val sentences = EditorUtils.extractKeySentences(message)
        Assert.assertTrue(sentences.size == 4)
        for (sentence in sentences) {
            Assert.assertTrue(sentence.endsWith("."))
        }
    }
}