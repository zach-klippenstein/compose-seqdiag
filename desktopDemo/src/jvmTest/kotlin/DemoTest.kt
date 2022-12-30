import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.zachklipp.seqdiag.samples.DemoApp
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class DemoTest {

    @get:Rule
    val rule = createComposeRule()

    @Ignore("https://youtrack.jetbrains.com/issue/KT-54634")
    @Test
    fun runs() {
        rule.setContent {
            DemoApp()
        }

        rule.onNodeWithText("Start the sequence, vroom vroom!").assertIsDisplayed()
    }
}