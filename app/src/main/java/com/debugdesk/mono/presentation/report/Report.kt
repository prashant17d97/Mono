package com.debugdesk.mono.presentation.report

import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.model.Tabs
import com.debugdesk.mono.presentation.uicomponents.CalendarBar
import com.debugdesk.mono.presentation.uicomponents.CategoryCard
import com.debugdesk.mono.presentation.uicomponents.DropDown
import com.debugdesk.mono.presentation.uicomponents.ExpenseCard
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import com.debugdesk.mono.presentation.uicomponents.NoDataFoundLayout
import com.debugdesk.mono.presentation.uicomponents.PermissionLauncherHandler
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.SpacerHeight
import com.debugdesk.mono.utils.CameraFunction.getNotificationPermissionHandler
import com.debugdesk.mono.utils.CommonColor.disableButton
import com.debugdesk.mono.utils.Dp
import com.debugdesk.mono.utils.Dp.dp0
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp2
import com.debugdesk.mono.utils.Dp.dp40
import com.debugdesk.mono.utils.Dp.dp5
import com.debugdesk.mono.utils.Dp.dp8
import com.debugdesk.mono.utils.Dp.dp84
import org.koin.androidx.compose.koinViewModel

@Composable
fun Report(
    navHostController: NavHostController, viewModel: ReportVM = koinViewModel()
) {
    val scroll = rememberScrollState(0)
    val reportState by viewModel.reportState.collectAsState()
    val context = LocalContext.current
    BackHandler {
        (context as Activity).finishAffinity()
    }

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
        PermissionLauncherHandler(
            permissionHandler = getNotificationPermissionHandler(viewModel.appState),
        )
    }
    
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchTransaction()
        viewModel.updateView(
            navHostController = navHostController,
            reportView = ReportView.MonthlyReport
        )
    }
    ReportContainer(scrollState = scroll,
        reportState = reportState,
        onIntentChange = { reportIntent ->
            viewModel.updateReportState(
                context = context,
                reportIntent = reportIntent,
                navHostController = navHostController
            )
        })
}


@Composable
private fun ReportContainer(
    scrollState: ScrollState = rememberScrollState(),
    reportState: ReportState,
    currency: String = stringResource(id = R.string.inrIcon),
    onIntentChange: (ReportIntent) -> Unit
) {
    MonoColumn(
        modifier = Modifier.fillMaxSize(),
        isScrollEnabled = false,
        top = dp10,
        start = dp0,
        end = dp0,
        header = {
            ReportViewDropDown(
                reportState = reportState,
                onSelected = onIntentChange
            )
        }
    ) {
        AnimatedContent(
            targetState = reportState.selectedReportView,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut())
                } else {
                    (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> height } + fadeOut())
                }.using(
                    SizeTransform(clip = false)
                )
            },
            label = ""
        ) {
            when (it) {
                ReportView.MonthlyReport.stringValue -> MonthReport(
                    scrollState = scrollState,
                    reportState = reportState,
                    onIntentChange = onIntentChange
                )

                ReportView.CategoryReport.stringValue -> CategoryReport(
                    reportState = reportState,
                    onIntentChange = onIntentChange
                )

                else -> {}
            }

        }

    }
}

@Composable
private fun MonthReport(
    scrollState: ScrollState = rememberScrollState(),
    reportState: ReportState,
    onIntentChange: (ReportIntent) -> Unit
) {
    MonoColumn(scrollState = scrollState,
        verticalArrangement = Arrangement.spacedBy(dp10, Alignment.Top),
        header = {}
    ) {
        CalendarBar(
            modifier = Modifier.padding(dp0),
            reportState = reportState,
            onIntentChange = onIntentChange
        )

        TotalLeftBalanceCard(
            currency = stringResource(id = reportState.currency), amount = reportState.totalAmount
        )

        MonthBalanceSummary(
            currency = stringResource(id = reportState.currency),
            income = reportState.currentMonthIncome,
            expense = reportState.currentMonthExpense
        )
        CurrentMonthAvlBalance(
            currency = stringResource(id = reportState.currency),
            currentMonth = reportState.monthString,
            availableBalance = reportState.currentMonthAvailableBalance
        )

        TabRow(tabsList = reportState.tabs, onTabSelected = { _, index ->
            onIntentChange(ReportIntent.UpdateTab(index))
        })

        NoDataFoundLayout(
            show = reportState.isTransactionEmpty,
            text = R.string.noTransactionFound,
            imageSize = Dp.dp120,
            modifier = Modifier.padding(top = Dp.dp80),
            content = {
                Column {
                    reportState.distributedTransaction.forEach { (_, dailyTransaction) ->
                        ExpenseCard(currency = stringResource(id = reportState.currency),
                            dailyTransaction = dailyTransaction,
                            onTap = { onIntentChange(ReportIntent.EditTransaction(it.transactionId)) })
                    }
                }
            }
        )
    }
}

@Composable
private fun CategoryReport(
    reportState: ReportState,
    onIntentChange: (ReportIntent) -> Unit
) {
    MonoColumn(
        verticalArrangement = Arrangement.Top,
        isScrollEnabled = false,
        trailingColor = disableButton,
    ) {
        Text(
            text = stringResource(id = R.string.expense),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = dp2, vertical = dp5)
        )

        LazyVerticalGrid(columns = GridCells.Adaptive(Dp.dp70)) {
            items(
                reportState.expenseCategory
            ) { model ->
                CategoryCard(
                    model = model,
                    onClick = {
                        onIntentChange(ReportIntent.NavigateToGraph(it.categoryId))
                    }
                )
            }

        }

        SpacerHeight(value = dp10)
        Text(
            text = stringResource(id = R.string.income),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 5.dp)
        )
        LazyVerticalGrid(columns = GridCells.Adaptive(Dp.dp70)) {
            items(
                reportState.incomeCategory
            ) { model ->
                CategoryCard(
                    model = model,
                    onClick = {
                        onIntentChange(ReportIntent.NavigateToGraph(it.categoryId))
                    }
                )
            }
        }

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
            text = "$currency $availableBalance", modifier = Modifier.padding(dp8)
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

@Composable
fun ReportViewDropDown(
    modifier: Modifier = Modifier,
    reportState: ReportState,
    onSelected: (ReportIntent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dp10)
            .clickable { expanded = !expanded },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = reportState.selectedReportView),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        DropDown(
            selectedValue = stringResource(id = reportState.selectedReportView),
            items = reportState.reportView.map { stringResource(id = it.stringValue) },
            expanded = expanded,
            onSelected = { _, index ->
                onSelected(ReportIntent.ChangeReportView(reportState.reportView[index]))
            },
            onExpend = { expanded = it },
            heading = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_caret_down),
                    contentDescription = "",
                )
            }
        )
    }

}

@Preview
@Composable
fun ReportPreview() = PreviewTheme {
    ReportContainer(scrollState = rememberScrollState(),
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

