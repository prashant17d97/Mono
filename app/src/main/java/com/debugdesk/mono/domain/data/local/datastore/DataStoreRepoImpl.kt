package com.debugdesk.mono.domain.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.StateFlow

class DataStoreRepoImpl(private val dataStoreUtil: DataStoreUtil) : DataStoreRepo {

    companion object {
        private val APP_CONFIG_PROPERTIES by lazy { stringPreferencesKey("AppConfigProperties") }
        private val BOOLEAN_DATA by lazy { booleanPreferencesKey("BOOLEAN") }
        private val INTRO_FINISHED by lazy { booleanPreferencesKey("BOOLEAN") }
        private val LOGIN_DATA by lazy { stringPreferencesKey("LOGIN_DATA") }
        private val USER_CREDS by lazy { stringPreferencesKey("USER_CREDS") }
        private val REMEMBER by lazy { booleanPreferencesKey("REMEMBER") }
        private val LANGUAGE by lazy { stringPreferencesKey("LANGUAGE") }
    }

    override val isIntroFinished: StateFlow<Boolean>
        get() = TODO("Not yet implemented")
}