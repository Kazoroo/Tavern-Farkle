package pl.kazoroo.tavernFarkle.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class UserDataRepository(private val dataStore: DataStore<Preferences>) {

    suspend fun saveNewValue(key: UserDataKey, value: String) {
        val dataStoreKey = stringPreferencesKey(key.name)

        dataStore.edit { userData ->
            userData[dataStoreKey] = value
        }
    }

    suspend fun readValue(key: UserDataKey): String? {
        val dataStoreKey = stringPreferencesKey(key.name)
        val preferences = dataStore.data.first()

        return if(dataStoreKey.name == UserDataKey.COINS.name && preferences[dataStoreKey] == null) {
            "200"
        } else {
            preferences[dataStoreKey]
        }
    }
}