package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.utils.CommonColor.inActiveButton
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp24
import com.debugdesk.mono.utils.Dp.dp5
import com.debugdesk.mono.utils.Dp.dp6
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getExpenseAmount
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getIncomeAmount
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toDate
import com.debugdesk.mono.utils.enums.ExpenseType

@Composable
fun ExpenseCard(
    currency: String,
    dailyTransaction: List<DailyTransaction>,
    onTap: (transitionId: DailyTransaction) -> Unit = {}
) {

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = dailyTransaction[0].date.toDate(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
            ) {
                if (dailyTransaction.getExpenseAmount() > 0.0) {
                    Text(
                        text = "+$currency ${dailyTransaction.getIncomeAmount()}",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                if (dailyTransaction.getIncomeAmount() > 0.0) {
                    SpacerHeight(value = dp5)
                    Text(
                        text = "-$currency ${dailyTransaction.getExpenseAmount()}",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }

        dailyTransaction.forEachIndexed { _, incomeExpenseModel ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dp10, vertical = dp6)
                    .clickable { onTap(incomeExpenseModel) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = incomeExpenseModel.categoryIcon),
                        contentDescription = "Category",
                        modifier = Modifier.size(dp24),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                    )

                    Text(
                        text = incomeExpenseModel.notes,
                        modifier = Modifier.padding(start = 5.dp, end = 3.dp)
                    )


                }
                Text(
                    text = "${"+".takeIf { incomeExpenseModel.type == ExpenseType.Income.name } ?: "-"}${currency} ${incomeExpenseModel.amount}",
                    color = MaterialTheme.colorScheme.primary.takeIf { incomeExpenseModel.type == ExpenseType.Income.name }
                        ?: inActiveButton,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ExpenseCardPrev() {
    PreviewTheme {
        ExpenseCard(
            currency = "₹",
            dailyTransaction = listOf(
                DailyTransaction(
                    date = 0,
                    type = ExpenseType.Expense.name,
                    note = "Coffee",
                    category = "Bevarages",
                    categoryIcon = R.drawable.ic_category,
                    amount = 2000.0,
                    images = listOf("", ""),
                    currentMonthId = 0,
                    categoryId = 6
                )
            )
        )
    }
}
