package com.debugdesk.mono.presentation.setting.currency

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.CurrencyCard
import com.debugdesk.mono.presentation.uicomponents.ScreenView
import com.debugdesk.mono.presentation.uicomponents.SpacerHeight
import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties
import com.debugdesk.mono.utils.CommonColor.disableButton
import com.debugdesk.mono.utils.Dp.dp5
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toCurrencyIcon
import org.koin.androidx.compose.koinViewModel


const val TAG = "Currency"

@Composable
fun Currency(
    navHostController: NavHostController,
    currencyVM: CurrencyVM = koinViewModel()
) {

    val appConfigProperties by currencyVM.appConfigProperties.collectAsState()

    val currency = appConfigProperties.currencyIcon

    BackHandler {
        currencyVM.revertTheAppConfigPropertiesChange()
        navHostController.popBackStack()
    }
    ScreenView(heading = stringResource(id = R.string.currency),
        showBack = true,
        trailing = stringResource(id = R.string.save),
        onBackClick = {
            navHostController.popBackStack()
            currencyVM.revertTheAppConfigPropertiesChange()
        },
        onTrailClick = {
            currencyVM.saveCurrencyChange()
        }) {
        getCurrencies(appConfigProperties).forEachIndexed { index, radioModel ->
            CurrencyCard(
                radioModel = radioModel,
                onClick = {
                    currencyVM.changeCurrency(
                        appConfigProperties = appConfigProperties.copy(
                            selectedCurrencyCode = it.currencyCode,
                        )
                    )
                })
            SpacerHeight(value = dp5)
            if (index != currency.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier
                        .height(1.dp)
                        .padding(start = 32.dp)
                        .fillMaxWidth()
                        .background(color = disableButton)
                )
                SpacerHeight(value = dp5)
            }
        }
    }
}

@Preview
@Composable
fun CurrencyPreview() = Currency(
    navHostController = rememberNavController()
)

@Composable
private fun getCurrencies(appConfigProperties: AppConfigProperties): List<RadioModel> {
    val context = LocalContext.current
    val currencyCodes = context.resources.getStringArray(R.array.currencyCode)

    return currencyCodes.map { code ->
        RadioModel(
            currencyIcon = code.toCurrencyIcon(),
            currencyCode = code,
            isSelected = appConfigProperties.selectedCurrencyCode == code
        )

    }
}

