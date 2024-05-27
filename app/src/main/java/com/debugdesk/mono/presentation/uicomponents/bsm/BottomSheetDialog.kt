package com.debugdesk.mono.presentation.uicomponents.bsm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.SpacerHeight
import com.debugdesk.mono.utils.Dp.dp10
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDialog(
    modifier: Modifier = Modifier,
    show: Boolean = false,
    onDismiss: (Boolean) -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    bottomSheetScaffoldState: SheetState = rememberModalBottomSheetState()
) {
    val scope = rememberCoroutineScope()
    AnimatedVisibility(visible = show) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss(false) },
            sheetState = bottomSheetScaffoldState,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            scope
                                .launch { bottomSheetScaffoldState.hide() }
                                .invokeOnCompletion {
                                    if (!bottomSheetScaffoldState.isVisible) {
                                        onDismiss(false)
                                    }
                                    onCamera()
                                }
                        }
                        .padding(10.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = stringResource(id = R.string.camera),
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.size(60.dp)
                    )

                    SpacerHeight(value = dp10)
                    Text(
                        text = stringResource(id = R.string.camera),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            scope
                                .launch { bottomSheetScaffoldState.hide() }
                                .invokeOnCompletion {
                                    if (!bottomSheetScaffoldState.isVisible) {
                                        onDismiss(false)
                                    }
                                    onGallery()
                                }

                        }
                        .padding(10.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_gallary),
                        contentDescription = stringResource(id = R.string.gallery),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.size(60.dp)
                    )
                    SpacerHeight(value = dp10)
                    Text(
                        text = stringResource(id = R.string.gallery),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

        }
    }
}