package com.debugdesk.mono.domain.data.local.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataStoreUtil(
    private val dataStore: DataStore<Preferences>,
) {
    fun <Generic> saveObject(key: Preferences.Key<String>, value: Generic) {
        CoroutineScope(Dispatchers.IO + exceptionHandler()).launch {
            dataStore.edit { preferences ->
                preferences[key] = Gson().toJson(value)
            }
        }
    }

    fun <T> retrieveObject(key: Preferences.Key<String>, `class`: Class<T>, valueIs: (T?) -> Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler()).launch {
            dataStore.edit {
                CoroutineScope(Dispatchers.Main).launch {
                    valueIs(Gson().fromJson(it[key], `class`))
                }
            }
        }
    }


    fun <Generic> saveKey(key: Preferences.Key<Generic>, value: Generic) {
        CoroutineScope(Dispatchers.IO + exceptionHandler()).launch {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    fun <Generic> retrieveKey(key: Preferences.Key<Generic>, valueIs: (Generic?) -> Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler()).launch {
            dataStore.edit {
                CoroutineScope(Dispatchers.Main).launch {
                    valueIs(it[key])
                }
            }
        }
    }

    fun clearDataStore(valueIs: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler()).launch {
            dataStore.edit { preferences ->
                preferences.clear()
            }
            CoroutineScope(Dispatchers.Main).launch {
                Log.d("clearDataStore", "clearDataStore")
                valueIs(true)
            }
        }
    }

    private fun exceptionHandler() = CoroutineExceptionHandler { _, t ->
        Log.e("DataStoreUtil", "exceptionHandler: ${t.localizedMessage}", )

        CoroutineScope(Dispatchers.Main).launch {
            Log.e("DataStoreUtil", "exceptionHandlerScope: ${t.localizedMessage}", )

            t.printStackTrace()
        }
    }
}