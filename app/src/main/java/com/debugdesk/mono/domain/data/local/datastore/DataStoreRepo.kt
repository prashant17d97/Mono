package com.debugdesk.mono.domain.data.local.datastore

import kotlinx.coroutines.flow.StateFlow

interface DataStoreRepo {
    val isIntroFinished: StateFlow<Boolean>
}