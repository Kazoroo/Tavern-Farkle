package pl.kazoroo.tavernFarkle.core.data.local.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
    }

    suspend fun <T : Any> saveValue(value: T, key: UserDataKey) {
        when (key) {
            UserDataKey.COINS -> saveString(value as String, key)
            UserDataKey.IS_SOUND_ENABLED,
            UserDataKey.IS_MUSIC_ENABLED,
            UserDataKey.IS_FIRST_LAUNCH,
            UserDataKey.IS_FIRST_GAME -> saveBoolean(value as Boolean, key)
        }
    }

    private suspend fun saveString(value: String, key: UserDataKey) {
        dataStore.edit { it[stringPreferencesKey(key.name)] = value }
    }

    private suspend fun saveBoolean(value: Boolean, key: UserDataKey) {
        dataStore.edit { it[booleanPreferencesKey(key.name)] = value }
    }

    fun getStringFlow(key: UserDataKey): Flow<String?> {
        return dataStore.data.map { it[stringPreferencesKey(key.name)] }
    }

    fun getBooleanFlow(key: UserDataKey): Flow<Boolean?> {
        return dataStore.data.map { it[booleanPreferencesKey(key.name)] }
    }
}