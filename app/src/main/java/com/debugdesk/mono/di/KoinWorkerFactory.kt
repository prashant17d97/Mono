package com.debugdesk.mono.di

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent

class KoinWorkerFactory : WorkerFactory(), KoinComponent {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): Worker? {
        return try {
            val workerClass = Class.forName(workerClassName).asSubclass(Worker::class.java)
            val constructor = workerClass.getDeclaredConstructor(
                Context::class.java,
                WorkerParameters::class.java
            )
            constructor.newInstance(appContext, workerParameters)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}