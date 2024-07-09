package com.debugdesk.mono.di

import android.app.NotificationManager
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.debugdesk.mono.domain.data.local.datastore.DataStoreUtil
import com.debugdesk.mono.domain.data.local.localdatabase.AppDatabase
import com.debugdesk.mono.domain.repo.Repository
import com.debugdesk.mono.domain.repo.RepositoryImpl
import com.debugdesk.mono.main.MainViewModel
import com.debugdesk.mono.notification.NotificationHelper
import com.debugdesk.mono.notification.NotificationScheduler
import com.debugdesk.mono.presentation.addcategory.AddCategoryVM
import com.debugdesk.mono.presentation.calendar.CalendarPageVM
import com.debugdesk.mono.presentation.editcategory.EditCategoryVM
import com.debugdesk.mono.presentation.edittrans.EditTransactionVM
import com.debugdesk.mono.presentation.graph.GraphVM
import com.debugdesk.mono.presentation.input.InputVM
import com.debugdesk.mono.presentation.intro.IntroViewModel
import com.debugdesk.mono.presentation.report.ReportVM
import com.debugdesk.mono.presentation.setting.SettingVM
import com.debugdesk.mono.presentation.setting.appearance.AppearanceVM
import com.debugdesk.mono.presentation.setting.currency.CurrencyVM
import com.debugdesk.mono.presentation.setting.reminder.ReminderVM
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppConfigurationManagerImpl
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.ui.appconfig.AppStateManagerImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {
    private val Context.dataStore by preferencesDataStore("MonoDataStore")

    val appModule = module {
        factory { DataStoreUtil(androidApplication().applicationContext.dataStore) }
        factory { AppDatabase.getInstance(androidApplication().applicationContext) }
        factory { AppDatabase.getInstance(androidApplication().applicationContext).daoInterface() }
        single<Repository> {
            RepositoryImpl(
                daoInterface = get(), appDatabase = get()
            )
        }
        single<AppStateManager> { AppStateManagerImpl() }
        single<AppConfigManager> { AppConfigurationManagerImpl(dataStoreUtil = get()) }


        single<NotificationManager> {
            androidApplication().applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
        }
        single<NotificationHelper> {
            NotificationHelper(
                context = androidApplication().applicationContext,
                notificationManager = get()
            )
        }
        single<NotificationScheduler> {
            NotificationScheduler(
                context = androidApplication().applicationContext
            )
        }


        viewModel {
            AddCategoryVM(repository = get())
        }
        viewModel {
            EditCategoryVM(repository = get())
        }

        viewModel {
            CalendarPageVM(repository = get(), appConfigManager = get())
        }
        viewModel {
            GraphVM(repository = get(), appConfigManager = get())
        }
        viewModel {
            InputVM(
                repository = get(), appConfigManager = get(), appStateManager = get()
            )
        }
        viewModel {
            IntroViewModel(
                appConfigManager = get(), repository = get()
            )
        }
        viewModel {
            ReportVM(
                appConfigManager = get(), repository = get(), appStateManager = get()
            )
        }

        viewModel {
            SettingVM(
                appConfigManager = get(),
                repository = get(),
                appStateManager = get(),
            )
        }

        viewModel {
            AppearanceVM(
                appConfigManager = get(),
                appStateManager = get(),
            )
        }

        viewModel {
            CurrencyVM(
                appConfigManager = get()
            )
        }

        viewModel {
            ReminderVM(
                appStateManager = get(),
                notificationScheduler = get(),
                appConfigManager = get()
            )
        }

        viewModel {
            MainViewModel(
                appStateManager = get(), appConfigManager = get()
            )
        }

        viewModel {
            EditTransactionVM(
                appStateManager = get(),
                appConfigManager = get(),
                repository = get(),
            )
        }
    }
}