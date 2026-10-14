package dev.danielkindl.cistula.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.danielkindl.cistula.MainActivity
import dev.danielkindl.cistula.R
import dev.danielkindl.cistula.ui.theme.CistulaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CistulaAppTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsTheApplicationName() {
        val applicationName = composeTestRule.activity.getString(R.string.app_name)

        composeTestRule.onNodeWithText(applicationName).assertIsDisplayed()
    }

    @Test
    fun showsTheTitleFromTheContent() {
        val applicationName = composeTestRule.activity.getString(R.string.app_name)
        val content = CistulaAppViewModel(title = CistulaAppFixture.TITLE).content

        composeTestRule.setContent {
            CistulaTheme {
                CistulaApp(content = content)
            }
        }

        composeTestRule.onNodeWithText(CistulaAppFixture.TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithText(applicationName).assertDoesNotExist()
    }
}
