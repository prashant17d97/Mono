package com.debugdesk.mono.presentation.setting.appearance

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.model.FontModel
import com.debugdesk.mono.model.LanguageModel
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import com.debugdesk.mono.presentation.uicomponents.SpacerWidth
import com.debugdesk.mono.presentation.uicomponents.Switch2
import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties
import com.debugdesk.mono.ui.appconfig.defaultconfig.ThemeMode
import com.debugdesk.mono.utils.CommonColor.disableButton
import com.debugdesk.mono.utils.Dp
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getFontFamilies
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getLanguages
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.isDarkTheme
import org.koin.androidx.compose.koinViewModel

const val TAG = "Appearance"

@Composable
fun Appearance(
    navHostController: NavHostController,
    appearanceVM: AppearanceVM = koinViewModel()
) {
    val context = LocalContext.current
    val appConfigProperties by appearanceVM.appConfigProperties.collectAsState()

    LaunchedEffect(appConfigProperties) {
        Log.e(TAG, "Appearance: ${appConfigProperties.dynamicColor}")
    }

    var appearanceState by remember { mutableStateOf(AppearanceState()) }

    BackHandler {
        appearanceVM.revertTheAppConfigPropertiesChange()
        navHostController.popBackStack()
    }

    AppearanceContainer(
        onBack = {
            appearanceVM.revertTheAppConfigPropertiesChange()
            navHostController.popBackStack()
        },
        onSave = {
            appearanceVM.saveNewAppConfigPropertiesChanges()
            appearanceVM.showToastState(toastMsg = R.string.app_config_saved)
        },
        appConfigProperties = appConfigProperties,
        appearanceState = appearanceState,
        fontItems = context.getFontFamilies(appConfigProperties.fontFamily),
        languageModel = context.getLanguages(appConfigProperties.language),
        onAppConfigPropertiesChange = {
            appearanceVM.requestAppConfigChanges(it)
        },
        onAppearanceStateChanges = { appearanceState = it },
        requestDynamicColors = {
            appearanceVM
                .requestAppConfigChanges(
                    appConfigProperties = appConfigProperties.copy(
                        dynamicColor = !appConfigProperties.dynamicColor
                    )
                )
        })


}

@Composable
private fun AppearanceContainer(
    onBack: () -> Unit,
    onSave: () -> Unit,
    fontItems: List<FontModel>,
    languageModel: List<LanguageModel>,
    appConfigProperties: AppConfigProperties,
    onAppConfigPropertiesChange: (AppConfigProperties) -> Unit,
    appearanceState: AppearanceState,
    onAppearanceStateChanges: (AppearanceState) -> Unit,
    requestDynamicColors: (Boolean) -> Unit = {}
) {
    MonoColumn(
        heading = stringResource(id = R.string.appearance),
        showBack = true,
        isScrollEnabled = false,
        trailing = stringResource(id = R.string.save),
        onBackClick = onBack,
        onTrailClick = onSave
    ) {
        FontItems(
            title = stringResource(id = R.string.font),
            value = appConfigProperties.fontFamily,
            isExpanded = appearanceState.font,
            onToggleExpand = { onAppearanceStateChanges(appearanceState.copy(font = it)) },
            onItemClick = { onAppConfigPropertiesChange(appConfigProperties.copy(fontFamily = it)) },
            items = fontItems
        )

        LanguageItems(
            title = stringResource(id = R.string.language),
            value = appConfigProperties.language,
            isExpanded = appearanceState.language,
            onToggleExpand = { onAppearanceStateChanges(appearanceState.copy(language = it)) },
            onItemClick = { onAppConfigPropertiesChange(appConfigProperties.copy(language = it)) },
            items = languageModel
        )
        DropDownThemeMenu(
            isDropDownExpanded = appearanceState.theme,
            onDropDownClick = {
                onAppearanceStateChanges(appearanceState.copy(theme = !appearanceState.theme))
            },
            appConfigProperties = appConfigProperties,
            onAppConfigPropertiesChange = onAppConfigPropertiesChange
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColorSetting(
                value = appConfigProperties.dynamicColor,
                onValueChange = requestDynamicColors
            )
        }
    }
}

@Composable
private fun FontItems(
    title: String,
    value: String,
    isExpanded: Boolean,
    onToggleExpand: (Boolean) -> Unit,
    onItemClick: (String) -> Unit,
    items: List<FontModel>
) {
    Column(
        modifier = Modifier
            .padding(bottom = 5.dp)
            .border(width = 1.dp, shape = RoundedCornerShape(8.dp), color = disableButton)
    ) {
        Row(modifier = Modifier
            .clickable { onToggleExpand(!isExpanded) }
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(10.dp)
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .clickable { onItemClick(item.font) },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.font, style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily(
                                    Font(
                                        googleFont = GoogleFont(name = item.font),
                                        fontProvider = GoogleFont.Provider(
                                            providerAuthority = "com.google.android.gms.fonts",
                                            providerPackage = "com.google.android.gms",
                                            certificates = R.array.com_google_android_gms_fonts_certs
                                        )
                                    )
                                )
                            )
                        )

                        Icon(painter = painterResource(id = R.drawable.ic_right_tick),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary.takeIf { item.isSelected }
                                ?: Color.Transparent)
                    }
                }
            }
        }
    }
}


@Composable
private fun LanguageItems(
    title: String,
    value: String,
    isExpanded: Boolean,
    onToggleExpand: (Boolean) -> Unit,
    onItemClick: (String) -> Unit,
    items: List<LanguageModel>
) {
    Column(
        modifier = Modifier
            .padding(bottom = 5.dp)
            .border(width = 1.dp, shape = RoundedCornerShape(8.dp), color = disableButton)
    ) {
        Row(modifier = Modifier
            .clickable { onToggleExpand(!isExpanded) }
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(10.dp)
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .clickable { onItemClick(item.language) },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.language, style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily(
                                    Font(
                                        googleFont = GoogleFont(name = item.language),
                                        fontProvider = GoogleFont.Provider(
                                            providerAuthority = "com.google.android.gms.fonts",
                                            providerPackage = "com.google.android.gms",
                                            certificates = R.array.com_google_android_gms_fonts_certs
                                        )
                                    )
                                )
                            )
                        )

                        Icon(painter = painterResource(id = R.drawable.ic_right_tick),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary.takeIf { item.isSelected }
                                ?: Color.Transparent)
                    }
                }
            }
        }
    }
}

@Composable
private fun DynamicColorSetting(
    value: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .border(width = 1.dp, shape = RoundedCornerShape(8.dp), color = disableButton)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.dynamicColor),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(10.dp)
            )
            Switch2(
                value = value,
                onValueChange = onValueChange,
                width = 20.dp,
                height = 10.dp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun DropDownThemeMenu(
    isDropDownExpanded: Boolean,
    onDropDownClick: () -> Unit,
    appConfigProperties: AppConfigProperties,
    onAppConfigPropertiesChange: (AppConfigProperties) -> Unit = {}
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 5.dp)
        .clickable { onDropDownClick() }
        .border(width = 1.dp, shape = RoundedCornerShape(8.dp), color = disableButton),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .width(200.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(id = R.drawable.ic_dark_mode.takeIf { appConfigProperties.isDarkTheme.isDarkTheme() }
                ?: R.drawable.ic_mode_light),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary))
            SpacerWidth(value = Dp.dp5)
            Text(
                text = stringResource(id = R.string.dark_mode),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Image(painter = painterResource(id = R.drawable.ic_caret_right),
            contentDescription = "ic_caret_right",
            modifier = Modifier
                .size(width = 34.dp, height = 24.dp)
                .padding(end = 10.dp)
                .rotate(90f.takeIf { isDropDownExpanded } ?: 0f),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary))
    }
    DropdownMenu(expanded = isDropDownExpanded, offset = DpOffset(
        x = 0.dp, y = (-60).dp
    ), properties = PopupProperties(
        dismissOnBackPress = true, dismissOnClickOutside = true
    ), onDismissRequest = { onDropDownClick() }) {
        ThemeMode.entries.forEach { menu ->
            DropdownMenuItem(onClick = {
                onAppConfigPropertiesChange(appConfigProperties.copy(isDarkTheme = menu))
                onDropDownClick()
            }, text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(id = menu.stringRes))
                    SpacerWidth(value = Dp.dp6)
                    AnimatedVisibility(
                        visible = menu.stringRes == appConfigProperties.isDarkTheme.stringRes
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected Theme"
                        )
                    }
                }
            })
        }
    }
}


@Preview
@Composable
fun AppearancePre() {
    val context = LocalContext.current
    AppearanceContainer(onBack = { },
        onSave = { },
        appConfigProperties = AppConfigProperties(),
        appearanceState = AppearanceState(),
        fontItems = context.getFontFamilies("Poppins"),
        languageModel = context.getLanguages("English"),
        onAppConfigPropertiesChange = { },
        onAppearanceStateChanges = { })
}