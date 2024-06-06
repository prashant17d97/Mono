package com.debugdesk.mono.presentation.report

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.model.Tabs
import com.debugdesk.mono.presentation.uicomponents.CalendarBar
import com.debugdesk.mono.presentation.uicomponents.ExpenseCard
import com.debugdesk.mono.presentation.uicomponents.NoDataFound
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.ScreenView
import com.debugdesk.mono.presentation.uicomponents.TransactionCard
import com.debugdesk.mono.utils.CommonColor.disableButton
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp120
import com.debugdesk.mono.utils.Dp.dp40
import com.debugdesk.mono.utils.Dp.dp8
import com.debugdesk.mono.utils.Dp.dp80
import com.debugdesk.mono.utils.Dp.dp84
import org.koin.androidx.compose.koinViewModel

@Composable
fun Report(
    navHostController: NavHostController,
    viewModel: ReportVM = koinViewModel()
) {
    val scroll = rememberScrollState(0)
    val reportState by viewModel.reportState.collectAsState()
    val context = LocalContext.current
    BackHandler {
        (context as Activity).finishAffinity()
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchTransaction()
    }
    ReportContainer(
        scrollState = scroll,
        reportState = reportState,
        onIntentChange = {
            viewModel.updateReportState(
                context = context,
                reportIntent = it,
                navHostController = navHostController
            )
        }
    )
}


@Composable
private fun ReportContainer(
    scrollState: ScrollState = rememberScrollState(),
    reportState: ReportState,
    currency: String = stringResource(id = reportState.currency),
    onIntentChange: (ReportIntent) -> Unit
) {
    if (reportState.showTransactionCard) {
        TransactionCard(
            currency = currency,
            dailyTransaction = reportState.showClickedTransaction,
            onIntentChange = onIntentChange
        )
    }
    ScreenView(
        scrollState = scrollState,
        verticalArrangement = Arrangement.spacedBy(dp10, Alignment.Top),
    ) {
        CalendarBar(
            modifier = Modifier.padding(0.dp),
            reportState = reportState,
            onIntentChange = onIntentChange
        )

        TotalLeftBalanceCard(
            currency = currency,
            amount = reportState.totalAmount
        )

        MonthBalanceSummary(
            currency = currency,
            income = reportState.currentMonthIncome,
            expense = reportState.currentMonthExpense
        )
        CurrentMonthAvlBalance(
            currency = currency,
            currentMonth = reportState.monthString,
            availableBalance = reportState.currentMonthAvailableBalance
        )

        TabRow(tabsList = reportState.tabs, onTabSelected = { _, index ->
            onIntentChange(ReportIntent.UpdateTab(index))
        })

        if (reportState.isTransactionEmpty) {
            Column {
                reportState.distributedTransaction.forEach { (_, dailyTransaction) ->
                    ExpenseCard(
                        currency = currency,
                        dailyTransaction = dailyTransaction,
                        onTap = { onIntentChange(ReportIntent.EditTransaction(it.transactionId)) }
                    )
                }
            }
        }

        NoDataFound(
            show = !reportState.isTransactionEmpty,
            text = R.string.noTransactionFound,
            imageSize = dp120,
            modifier = Modifier.padding(top = dp80)
        )
    }
}

@Composable
private fun TotalLeftBalanceCard(
    currency: String, amount: Double = 0.0
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(dp40)
            .border(width = 1.dp, color = disableButton, shape = RoundedCornerShape(dp8))
    ) {
        Text(
            text = stringResource(id = R.string.totalBalance),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(dp8)
        )
        Text(
            text = "$currency $amount",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(dp8)
        )
    }
}

@Composable
private fun MonthBalanceSummary(
    currency: String, income: Double = 0.0, expense: Double = 0.0
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .height(dp84)
            .border(
                width = 1.dp, color = disableButton, shape = RoundedCornerShape(dp8)
            )
            .padding(dp8)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.expense),
                style = MaterialTheme.typography.bodyMedium,

                )
            Text(
                text = "-$currency $expense",
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.income),
                style = MaterialTheme.typography.bodyMedium,

                )
            Text(
                text = "+$currency $income",
            )
        }
    }
}

@Composable
private fun CurrentMonthAvlBalance(
    currency: String,
    currentMonth: String,
    availableBalance: Double = 0.0,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp, color = disableButton, shape = RoundedCornerShape(dp8)
            )
    ) {
        Text(
            text = stringResource(id = R.string.thisMonthTotalBalance, currentMonth),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(dp8)
        )
        Text(
            text = "$currency $availableBalance",
            modifier = Modifier.padding(dp8)
        )
    }
}

@Composable
fun TabRow(
    tabsList: List<Tabs>, onTabSelected: (tabs: Tabs, index: Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.border(
                width = 1.dp,
                color = disableButton,
                shape = RoundedCornerShape(topStart = dp8, topEnd = dp8)
            )
        ) {
            tabsList.forEachIndexed { index, tabs ->
                Box(modifier = Modifier
                    .clickable {
                        onTabSelected(tabs, index)
                    }
                    .background(color = MaterialTheme.colorScheme.primaryContainer.takeIf { tabs.isSelected }
                        ?: Color.Transparent,
                        shape = RoundedCornerShape(topStart = dp8.takeIf { index == 0 } ?: 0.dp,
                            topEnd = 0.dp.takeIf { index != tabsList.size - 1 }
                                ?: dp8).takeIf { tabs.isSelected } ?: RoundedCornerShape(0.dp))) {
                    Text(
                        text = stringResource(id = tabs.text),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = dp8, horizontal = 15.dp)
                    )
                }

                if (index != tabsList.size - 1) {
                    Spacer(
                        Modifier
                            .width(0.dp.takeIf { index == 2 || tabs.isSelected } ?: 1.dp)
                            .height(8.dp)
                            .background(disableButton)
                            .padding(vertical = 2.dp))
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = disableButton)
                .weight(1f)
        )
    }
}


@Preview
@Composable
fun ReportPreview() = PreviewTheme {
    ReportContainer(
        scrollState = rememberScrollState(),
        reportState = ReportState(),
        onIntentChange = { })
}


@Preview
@Composable
fun TopLeftBalancePreview() = PreviewTheme {
    Column(verticalArrangement = Arrangement.spacedBy(dp10, Alignment.Top)) {
        TotalLeftBalanceCard(currency = "$", amount = 8.9)
        MonthBalanceSummary(currency = "$", income = 12.13, expense = 10.11)
        CurrentMonthAvlBalance(currency = "$", currentMonth = "January", availableBalance = 14.15)
    }
}

