package pl.kazoroo.tavernFarkle.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository
import pl.kazoroo.tavernFarkle.core.data.local.repository.dataStore
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

        val testDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO),
            produceFile = { testDataStoreFile }
        )

        testContext = mock(Context::class.java)
        `when`(testContext.dataStore).thenReturn(testDataStore)

        userDataRepository = UserDataRepository.getInstance(testContext)
    }

    @Test
    fun saveNewValue() = runTest {
        val coins = "1100"

        userDataRepository.saveNewValue(value = coins)
        advanceUntilIdle()
    }

    @Test
    fun checkForDefaultValue() = runTest {
        assertEquals(null, userDataRepository.userCoins.first())
        advanceUntilIdle()
    }

    @Test
    fun saveAndReadValue() = runTest {
        val coins = "1100"

        userDataRepository.saveNewValue(value = coins)
        assertEquals(coins, userDataRepository.userCoins.first())
        advanceUntilIdle()
    }
}