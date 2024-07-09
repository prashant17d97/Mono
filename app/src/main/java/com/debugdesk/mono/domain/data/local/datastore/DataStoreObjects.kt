package com.debugdesk.mono.domain.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreObjects {
    val APP_CONFIG_PROPERTIES by lazy { stringPreferencesKey("AppConfigProperties") }
    val INTRO_FINISHED by lazy { booleanPreferencesKey("BOOLEAN") }
    val REMINDER_TIME by lazy { stringPreferencesKey("REMINDER_TIME") }
}