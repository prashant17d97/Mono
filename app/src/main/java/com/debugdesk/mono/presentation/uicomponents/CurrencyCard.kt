package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.presentation.setting.currency.RadioModel
import com.debugdesk.mono.utils.CommonColor.disableButton

@Composable
fun CurrencyCard(
    radioModel: RadioModel,
    onClick: (RadioModel) -> Unit
) {
    Row(modifier = Modifier
        .clickable { onClick(radioModel) }
        .fillMaxWidth()
        .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        RadioButton(
            selected = radioModel.isSelected,
            onClick = { onClick(radioModel) },
            modifier = Modifier
                .padding(0.dp)
                .weight(0.25f),
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = disableButton
            )
        )
        Row(
            modifier = Modifier
                .weight(3f)
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .border(
                        width = 1.dp, color = disableButton, shape = RoundedCornerShape(5.dp)
                    )
                    .size(35.dp)
            ) {
                Text(
                    text = radioModel.currencyIcon,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "${radioModel.currencyIcon}${radioModel.defaultCurrencyValue}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f),
                textAlign = TextAlign.Start
            )
        }

        Text(
            text = radioModel.currencyCode,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}