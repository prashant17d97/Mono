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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.debugdesk.mono.R
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.ScreenView
import com.debugdesk.mono.presentation.uicomponents.SpacerWidth
import com.debugdesk.mono.ui.appconfig.defaultconfig.SettingModel
import com.debugdesk.mono.ui.appconfig.defaultconfig.SettingNameEnum
import com.debugdesk.mono.utils.CommonColor.disableButton
import com.debugdesk.mono.utils.CommonColor.inActiveButton
import com.debugdesk.mono.utils.Dp.dp5
import org.koin.androidx.compose.koinViewModel

@Composable
fun Setting(
    navHostController: NavHostController,
    settingVM: SettingVM = koinViewModel()
) {
    val appConfigProperties by settingVM.appConfigProperties.collectAsState()

    val allItems by settingVM.allDataCount.collectAsState()

    ScreenView(
        heading = stringResource(id = R.string.setting),
        onBackClick = { navHostController.popBackStack() }) {
        settingVM.settings(appConfigProperties.currencyIcon)
            .forEach { settingModel ->
                SettingItem(settingModel = settingModel, navHostController = navHostController)
            }
        DeleteAllRow(isDataAvailable = allItems > 0) {
            settingVM.deleteAllData()
        }
    }
}

@Composable
private fun SettingItem(
    settingModel: SettingModel,
    navHostController: NavHostController
) {
    Row(
        modifier = Modifier
            .clickable {
                navHostController.navigate(
                    when (settingModel.name) {
                        SettingNameEnum.Category -> Screens.EditCategory.route
                        SettingNameEnum.Currency -> Screens.Currency.route
                        SettingNameEnum.Reminder -> Screens.Reminder.route
                        SettingNameEnum.Appearance -> Screens.Appearance.route
                    }
                )
            }
            .fillMaxWidth()
            .padding(bottom = 5.dp)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(8.dp),
                color = disableButton
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = settingModel.icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            SpacerWidth(value = dp5)
            Text(
                text = stringResource(id = settingModel.name.stringRes),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_caret_right),
            contentDescription = "ic_caret_right",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary), // Adjust tint as needed
            modifier = Modifier
                .size(width = 34.dp, height = 24.dp)
                .padding(end = 10.dp)
        )
    }
}

@Composable
private fun DeleteAllRow(
    isDataAvailable: Boolean,
    onDeleted: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable(enabled = isDataAvailable) { onDeleted() } // Click only if enabled
            .fillMaxWidth()
            .padding(bottom = 5.dp)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(8.dp),
                color = disableButton
            ), // Update border color based on boolean
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_trash),
            contentDescription = null,
            modifier = Modifier
                .size(height = 40.dp, width = 40.dp)
                .padding(start = 10.dp, end = 5.dp, top = 10.dp, bottom = 10.dp),
            colorFilter = ColorFilter.tint(
                if (isDataAvailable) inActiveButton else inActiveButton.copy(
                    alpha = 0.5f
                )
            )
        )
        Text(
            text = stringResource(id = R.string.delete_all),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isDataAvailable) inActiveButton else inActiveButton.copy(
                    alpha = 0.5f
                )
            ) // Update text color based on boolean
        )
    }

}

@Preview
@Composable
fun SettingPreview() = PreviewTheme {
    Column {
        SettingItem(
            settingModel = SettingModel(
                icon = R.drawable.ic_dark_mode,
                name = SettingNameEnum.Category
            ), navHostController = rememberNavController()

        )
        DeleteAllRow(isDataAvailable = true, onDeleted = {})
        DeleteAllRow(isDataAvailable = false, onDeleted = {})

    }
}

@Preview
@Composable
fun SettingPreviewsLight() = PreviewTheme(isDarkTheme = false) {
    Column {
        SettingItem(
            settingModel = SettingModel(
                icon = R.drawable.ic_dark_mode,
                name = SettingNameEnum.Category
            ), navHostController = rememberNavController()

        )
        DeleteAllRow(isDataAvailable = true, onDeleted = {})
        DeleteAllRow(isDataAvailable = false, onDeleted = {})

    }
}