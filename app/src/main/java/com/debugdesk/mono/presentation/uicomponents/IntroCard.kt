package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.enums.Buttons

@Composable
fun IntroCard(
    modifier: Modifier = Modifier,
    current: Int = 1,
    total: Int = 3,
    painterResource: Int = R.drawable.intro_img_one,
    heading: String,
    description: String,
    skip: () -> Unit,
    onContinue: () -> Unit,
) {
//        val seconds by heading.collectAsState(initial = heading)
    val height = (LocalConfiguration.current).screenHeightDp
    val width = (LocalConfiguration.current).screenWidthDp
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width((width - 10).dp)
            .height(height = (height * 0.95).dp)
            .padding(horizontal = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "$current/$total", style = MaterialTheme.typography.bodyMedium)
            SkipButton(text = stringResource(id = R.string.skip), onClick = { skip() })
        }

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Image(
                painter = painterResource(id = painterResource),
                contentDescription = "IntroLogo",
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .height((height * 0.4).dp)
                    .padding(bottom = 10.dp)
            )
            Text(
                text = heading,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        CustomButton(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
            text = stringResource(id = R.string.conti).takeIf { current != total }
                ?: stringResource(id = R.string.getStarted),
            status = Buttons.Active,
            onClick = { onContinue() })

    }
}