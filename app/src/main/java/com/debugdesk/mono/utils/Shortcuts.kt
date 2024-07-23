package com.debugdesk.mono.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.debugdesk.mono.R
import com.debugdesk.mono.main.MainActivity
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.notification.NotificationObjects.TARGET_SCREEN

@RequiresApi(Build.VERSION_CODES.N_MR1)
object Shortcuts {
    fun Activity.initializeShortCuts() {
        val shortcutManager = this.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
        shortcutManager.dynamicShortcuts =
            listOf(
                getShortcut(
                    id = R.string.add_transaction,
                    shortLabel = R.string.add_transaction,
                    longLabel = R.string.add_transaction,
                    icon = R.drawable.wallet,
                    targetScreens = Screens.Input.route,
                ),
            )
    }

    private fun Activity.getShortcut(
        @StringRes
        id: Int,
        @StringRes
        shortLabel: Int,
        @StringRes
        longLabel: Int,
        @DrawableRes
        icon: Int,
        targetScreens: String,
    ) = ShortcutInfo.Builder(this, getString(id))
        .setShortLabel(getString(shortLabel))
        .setLongLabel(getString(longLabel))
        .setIcon(Icon.createWithResource(this, icon))
        .setIntent(
            Intent(this, MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra(TARGET_SCREEN, targetScreens)
            },
        )
        .build()
}
