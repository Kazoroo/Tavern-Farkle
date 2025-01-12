package pl.kazoroo.tavernFarkle.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class UserDataRepository(private val dataStore: DataStore<Preferences>) {

    suspend fun saveNewValue(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)

        dataStore.edit { userData ->
            userData[dataStoreKey] = value
        }
    }

    suspend fun readValue(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()

        return preferences[dataStoreKey]
    }
}