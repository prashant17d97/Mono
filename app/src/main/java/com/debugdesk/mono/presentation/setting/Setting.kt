package com.debugdesk.mono.presentation.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.debugdesk.mono.R
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.SpacerWidth
import com.debugdesk.mono.ui.appconfig.defaultconfig.SettingModel
import com.debugdesk.mono.ui.appconfig.defaultconfig.SettingNameEnum
import com.debugdesk.mono.utils.CommonColor.disableButton
import com.debugdesk.mono.utils.CommonColor.inActiveButton
import com.debugdesk.mono.utils.Dp.dp1
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp24
import com.debugdesk.mono.utils.Dp.dp34
import com.debugdesk.mono.utils.Dp.dp40
import com.debugdesk.mono.utils.Dp.dp5
import com.debugdesk.mono.utils.Dp.dp8
import org.koin.androidx.compose.koinViewModel

@Composable
fun Setting(
    navHostController: NavHostController,
    settingVM: SettingVM = koinViewModel(),
) {
    val appConfigProperties by settingVM.appConfigProperties.collectAsState()

    val allItems by settingVM.allDataCount.collectAsState()
    val context = LocalContext.current

    MonoColumn(
        heading = stringResource(id = R.string.setting),
        onBackClick = { navHostController.popBackStack() },
    ) {
        settingVM.settings(appConfigProperties.selectedCurrencyIconDrawable)
            .forEach { settingModel ->
                SettingItem(settingModel = settingModel, navHostController = navHostController)
            }
        DeleteAllRow(isDataAvailable = allItems > 0) {
            settingVM.deleteAllData(context = context)
        }
    }
}

@Composable
private fun SettingItem(
    settingModel: SettingModel,
    navHostController: NavHostController,
) {
    Row(
        modifier =
        Modifier
            .clickable {
                navHostController.navigate(
                    when (settingModel.name) {
                        SettingNameEnum.Category -> Screens.EditCategory.route
                        SettingNameEnum.Currency -> Screens.Currency.route
                        SettingNameEnum.Reminder -> Screens.Reminder.route
                        SettingNameEnum.Appearance -> Screens.Appearance.route
                    },
                )
            }
            .fillMaxWidth()
            .padding(bottom = dp5)
            .border(
                width = dp1,
                shape = RoundedCornerShape(dp8),
                color = disableButton,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier =
            Modifier
                .padding(dp10)
                .weight(1f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = settingModel.icon),
                contentDescription = null,
                modifier = Modifier.size(dp24),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            )
            SpacerWidth(value = dp5)
            Text(
                text = stringResource(id = settingModel.name.stringRes),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_caret_right),
            contentDescription = "ic_caret_right",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary), // Adjust tint as needed
            modifier =
            Modifier
                .size(width = dp34, height = dp24)
                .padding(end = dp10),
        )
    }
}

@Composable
private fun DeleteAllRow(
    isDataAvailable: Boolean,
    onDeleted: () -> Unit,
) {
    Row(
        modifier =
        Modifier
            .clickable(enabled = isDataAvailable) { onDeleted() } // Click only if enabled
            .fillMaxWidth()
            .padding(bottom = dp5)
            .border(
                width = dp1,
                shape = RoundedCornerShape(dp8),
                color = disableButton,
            ),
        // Update border color based on boolean
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_trash),
            contentDescription = null,
            modifier =
            Modifier
                .size(dp40)
                .padding(start = dp10, end = dp5, top = dp10, bottom = dp10),
            colorFilter =
            ColorFilter.tint(
                if (isDataAvailable) {
                    inActiveButton
                } else {
                    inActiveButton.copy(
                        alpha = 0.8f,
                    )
                },
            ),
        )
        Text(
            text = stringResource(id = R.string.delete_all),
            style =
            MaterialTheme.typography.titleMedium.copy(
                color =
                if (isDataAvailable) {
                    inActiveButton
                } else {
                    inActiveButton.copy(
                        alpha = 0.8f,
                    )
                },
            ), // Update text color based on boolean
        )
    }
}

@Preview
@Composable
fun SettingPreview() =
    PreviewTheme {
        Column {
            SettingItem(
                settingModel =
                SettingModel(
                    icon = R.drawable.ic_dark_mode,
                    name = SettingNameEnum.Category,
                ),
                navHostController = rememberNavController(),
            )
            DeleteAllRow(isDataAvailable = true, onDeleted = {})
            DeleteAllRow(isDataAvailable = false, onDeleted = {})
        }
    }

@Preview
@Composable
fun SettingPreviewsLight() =
    PreviewTheme(isDarkTheme = false) {
        Column {
            SettingItem(
                settingModel =
                SettingModel(
                    icon = R.drawable.ic_dark_mode,
                    name = SettingNameEnum.Category,
                ),
                navHostController = rememberNavController(),
            )
            DeleteAllRow(isDataAvailable = true, onDeleted = {})
            DeleteAllRow(isDataAvailable = false, onDeleted = {})
        }
    }
