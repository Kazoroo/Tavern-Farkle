package pl.kazoroo.tavernFarkle.core.data.local

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class UserDataRepositoryTest {
    @get:Rule
    val temporaryFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private lateinit var userDataRepository: UserDataRepository
    private lateinit var testDataStoreFile: File
    private lateinit var testContext: Context

    @Before
    fun setUp() {
        testDataStoreFile = temporaryFolder.newFile("user.preferences_pb")
        testContext = InstrumentationRegistry.getInstrumentation().targetContext
        userDataRepository = UserDataRepository.getInstance(testContext)
    }

    @Test
    fun saveNewValue() = runTest {
        val coins = "1100"

        userDataRepository.saveValue(coins, UserDataKey.COINS)
        advanceUntilIdle()
    }

    @Test
    fun checkForDefaultValue() = runTest {
        assertEquals(null, userDataRepository.getBooleanFlow(UserDataKey.IS_FIRST_LAUNCH).first())
        advanceUntilIdle()
    }

    @Test
    fun saveAndReadValue() = runTest {
        val coins = "1100"

        userDataRepository.saveValue(coins, UserDataKey.COINS)
        assertEquals(coins, userDataRepository.getStringFlow(UserDataKey.COINS).first())
        advanceUntilIdle()
    }
}