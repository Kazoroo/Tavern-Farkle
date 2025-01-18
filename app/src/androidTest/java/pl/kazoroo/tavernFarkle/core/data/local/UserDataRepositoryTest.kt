package pl.kazoroo.tavernFarkle.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @Before
    fun setUp() {
        testDataStoreFile = temporaryFolder.newFile("user.preferences_pb")

        val testDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO),
            produceFile = { testDataStoreFile }
        )
        userDataRepository = UserDataRepository(testDataStore)
    }

    @Test
    fun saveNewValue() = runTest {
        val coins = "1100"

        userDataRepository.saveNewValue(key = UserDataKey.COINS.name, value = coins)
        advanceUntilIdle()
    }

    @Test
    fun checkForDefaultValue() = runTest {
        assertEquals(null, userDataRepository.readValue(key = UserDataKey.COINS.name))
        advanceUntilIdle()
    }

    @Test
    fun saveAndReadValue() = runTest {
        val coins = "1100"

        userDataRepository.saveNewValue(key = UserDataKey.COINS.name, value = coins)
        assertEquals(coins, userDataRepository.readValue(key = UserDataKey.COINS.name))
        advanceUntilIdle()
    }
}