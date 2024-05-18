package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.CalculateMathExpression.Companion.calculate
import com.debugdesk.mono.utils.CalculatorEnum
import com.debugdesk.mono.utils.enums.CellCounts

@Composable
fun Calculator(
    dotCount: Int = 0,
    priorValue: String,
    onValueReturn: (CalculatorEnum, String) -> Unit = { _, _ -> }
) {
    val cardBackground = MaterialTheme.colorScheme.secondaryContainer
    var value by rememberSaveable { mutableStateOf(priorValue) }
    var isDotContained by rememberSaveable { mutableIntStateOf(dotCount) }
    var valueChange by rememberSaveable { mutableStateOf(priorValue) }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.Transparent, shape = RoundedCornerShape(10.dp)
            )
            .defaultMinSize(
                minHeight = 400.dp
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = cardBackground, shape = RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp,
                    )
                )
                .height(150.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .height(120.dp)
                    .weight(3.5f)
                    .defaultMinSize(
                        minHeight = 150.dp
                    )
            ) {
                Text(
                    text = value,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Text(
                    text = valueChange,
                    maxLines = 1,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .height(130.dp)
                    .width(1.dp)
                    .background(color = MaterialTheme.colorScheme.primary)
            )

            Icon(painter = painterResource(id = R.drawable.ic_backspace),
                contentDescription = "Backspace",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .clickable {

                        if (value.isNotBlank() && (value.length > 1)) {
                            value = value.substring(0, value.length - 1)
                            val update = value.replace("x", "*")
                            if (update.last() != '.') {
                                valueChange = when (update.last()) {
                                    '+', '-' -> (update + "0")
                                        .calculate()
                                        .toString()

                                    '*', '/' -> (update + "1")
                                        .calculate()
                                        .toString()

                                    else -> update
                                        .calculate()
                                        .toString()
                                }
                            }
                        } else {
                            isDotContained = 0
                            value = ""
                            valueChange = ""
                        }
                    })
        }

        val char = listOf(
            "1",
            "2",
            "3",
            "/",
            "4",
            "5",
            "6",
            "x",
            "7",
            "8",
            "9",
            "-",
            ".",
            "0",
            "00",
            "+",
        )
        VerticalGridCells(
            list = char,
            spanCounts = CellCounts.Four.int,
            top = 0.dp,
            bottom = 0.dp,
            start = 0.dp,
            end = 0.dp,
        ) { character, _, spanCount ->
            CharacterCard(character = character, itemPerRow = spanCount, action = { string ->
                isDotContained = if (string.contains(".")) {
                    isDotContained + 1
                } else if ((string.contains("+")) || (string.contains("-")) || (string.contains(
                        "x"
                    )) || (string.contains(
                        "/"
                    ))
                ) {
                    0
                } else {
                    0.takeIf { isDotContained < 1 } ?: 2
                }
                val filtered = string.takeIf { (isDotContained <= 1) && string.contains(".") }
                    ?: string.replace(".", "")

                if (value.isNotBlank() && filtered.isNotEmpty()) {
                    when (when (filtered.last()) {
                        '+', '-', 'x', '/' -> true
                        else -> false
                    } && when (value.last()) {
                        '+', '-', 'x', '/' -> true
                        else -> false
                    }) {
                        true -> value = value.dropLast(1) + filtered
                        false -> value += filtered
                    }
                } else {
                    value += when (filtered) {
                        "+", "-", "x", "/" -> ""
                        else -> filtered
                    }
                }
                val update = value.replace("x", "*")

                if (value.isNotBlank() /*&& value.length > 2*/) {
                    valueChange = when (character) {
                        "+", "-" -> (update + "0").calculate().toString()
                        "x", "/" -> (update + "1").calculate().toString()
                        "." -> valueChange
                        else -> update.calculate().toString()
                    }
                }
            })
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    cardBackground, shape = RoundedCornerShape(
                        bottomEnd = 10.dp, bottomStart = 10.dp
                    )
                )
                .padding(20.dp)
        ) {
            Text(text = stringResource(id = R.string.cancel),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable { onValueReturn(CalculatorEnum.Cancel, "") })
            Text(text = stringResource(id = R.string.clear),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable {
                    isDotContained = 0
                    value = ""
                    valueChange = ""
                })

            Text(text = stringResource(id = R.string.okay),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable {
                    onValueReturn(CalculatorEnum.Okay, valueChange.takeIf {
                        !valueChange.equals(
                            "0", true
                        ) && valueChange.isNotEmpty()
                    } ?: value.takeIf {
                        (value.length > 1 && !value.last().equals('.', true)) && (!value.equals(
                            "0", true
                        ) && value.isNotEmpty()) && !((value.contains("+") || value.contains(
                            "-"
                        )
//                                            || value.contains(".")
                                || value.contains("*") || value.contains("/") || value.contains(
                            "x"
                        )))
                    } ?: "")
                })
            Text(text = stringResource(id = R.string.equal),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable {
                    if (value.contains("+") || value.contains("-") || value.contains("*") || value.contains(
                            "/"
                        ) || value.contains("x")
                    ) {
                        isDotContained = 0
                        value = valueChange
                        valueChange = ""
                    }
                })
        }
    }

}