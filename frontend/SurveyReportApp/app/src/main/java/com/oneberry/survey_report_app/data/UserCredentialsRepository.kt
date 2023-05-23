package com.oneberry.survey_report_app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.goodforgod.gson.configuration.GsonFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


private const val USER_CREDENTIALS_NAME = "user_credentials"
private const val USER_CREDENTIALS_KEY_STRING = "user_credentials_json"

private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(USER_CREDENTIALS_NAME)


class UserCredentialsRepository(context: Context){
    private val GSON = GsonFactory().build()
    private val USER_CREDENTIALS_KEY =
        stringPreferencesKey(USER_CREDENTIALS_KEY_STRING)
    private val preference_datastore =
        context.applicationContext.dataStore

    val credentialsFlow: Flow<UserCredentials> = preference_datastore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            if (preferences[USER_CREDENTIALS_KEY] == null) UserCredentials()
            else GSON.fromJson(preferences[USER_CREDENTIALS_KEY], UserCredentials::class.java)
        }

    suspend fun saveToPreferencesStore(userCredentials: UserCredentials) {
        preference_datastore.edit { preferences ->
            preferences[USER_CREDENTIALS_KEY] = GSON.toJson(userCredentials)
        }
    }
}