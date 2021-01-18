package com.jesil.projectmvvm.todolist.data

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import com.jesil.projectmvvm.todolist.util.SortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferenceManager"

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore("user_preferences")

    val preferencesFlow = dataStore.data
            .catch { exception ->
                if (exception is IOException){
                    Log.d(TAG, "Error reading Preferences ")
                    emit(emptyPreferences())
                }else{
                    throw exception
                }
            }
            .map { preferences ->
                val sortOrder = SortOrder.valueOf(
                        preferences[PreferencesKey.SORT_ORDER] ?: SortOrder.BY_DATE.name
                )
                val hideCompleted = preferences[PreferencesKey.HIDE_COMPLETE] ?: false
                FilterPreference(sortOrder, hideCompleted)
            }

    suspend fun updateSortOrder(sortOrder: SortOrder){
        dataStore.edit { preferences ->
            preferences[PreferencesKey.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideComplete(hideComplete: Boolean){
        dataStore.edit { preferences ->
            preferences[PreferencesKey.HIDE_COMPLETE] = hideComplete
        }
    }

    private object PreferencesKey{
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETE = preferencesKey<Boolean>("hide_complete")
    }
}