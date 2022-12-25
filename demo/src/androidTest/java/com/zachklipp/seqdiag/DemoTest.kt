package com.zachklipp.seqdiag

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zachklipp.seqdiag.demo.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun demoLaunches() {
        rule.onNodeWithText("Start the sequence, vroom vroom!").assertIsDisplayed()
    }
}