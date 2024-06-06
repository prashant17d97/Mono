package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransaction
import com.debugdesk.mono.presentation.report.ReportIntent
import com.debugdesk.mono.utils.CommonColor
import com.debugdesk.mono.utils.Dp
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toDate

@Composable
fun TransactionCard(
    modifier: Modifier = Modifier,
    currency: String,
    dailyTransaction: DailyTransaction,
    onIntentChange: (ReportIntent) -> Unit = {}
) {
    PopUp(dismiss = { onIntentChange(ReportIntent.CloseTransactionCard) }) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(Dp.dp16)
                )
                .padding(Dp.dp10)
        ) {
            Text(
                text = stringResource(id = R.string.transaction_card_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dp.dp10),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$currency ${dailyTransaction.amount}",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Icon(
                    imageVector = Icons.Rounded.Delete, contentDescription = "Close",
                    tint = CommonColor.inActiveButton,
                    modifier = Modifier.clickable { onIntentChange(ReportIntent.DeleteTransaction) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    Dp.dp6,
                    alignment = Alignment.Start
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = dailyTransaction.categoryIcon),
                    contentDescription = "Close",
                    modifier = Modifier.clickable { })
                Text(
                    text = dailyTransaction.category,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = dailyTransaction.date.toDate(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dp.dp16),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = dailyTransaction.note,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dp.dp16),
                style = MaterialTheme.typography.bodyLarge,
            )

            LazyRow(content = {

                items(dailyTransaction.transactionImage) {
                    // Image
                }
            })

            Row(
                modifier = Modifier
                    .fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(
                    Dp.dp24,
                    alignment = Alignment.End
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.close),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable { onIntentChange(ReportIntent.CloseTransactionCard) }
                )

                Button(onClick = { onIntentChange(ReportIntent.EditTransaction(dailyTransaction.transactionId)) }) {
                    Text(
                        text = stringResource(id = R.string.edit),

                        )
                }

            }

        }
    }
}

@Preview
@Composable
fun TransactionCardPrev() {
    PreviewTheme {
        TransactionCard(
            dailyTransaction = emptyTransaction,
            currency = "$"
        )
    }
}