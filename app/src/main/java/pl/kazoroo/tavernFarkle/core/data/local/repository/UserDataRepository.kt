package pl.kazoroo.tavernFarkle.core.data.local.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

class UserDataRepository private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        @Volatile
        private var instance: UserDataRepository? = null

        fun getInstance(context: Context): UserDataRepository {
            return instance ?: synchronized(this) {
                instance ?: UserDataRepository(context.dataStore).also { instance = it }
            }
        }

        private val USER_COINS_KEY = stringPreferencesKey(UserDataKey.COINS.name)
    }

    suspend fun saveNewValue(value: String) {
        dataStore.edit { userData ->
            userData[USER_COINS_KEY] = value
        }
    }

    val userCoins: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_COINS_KEY]
    }
}