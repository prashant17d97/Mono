package com.debugdesk.mono.presentation.graph

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.BottomSheet
import com.debugdesk.mono.presentation.uicomponents.ExpenseCard
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import com.debugdesk.mono.presentation.uicomponents.NoDataFoundLayout
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.utils.CommonColor.disableButton
import com.debugdesk.mono.utils.Dp.dp0
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp100
import com.debugdesk.mono.utils.Dp.dp3
import com.debugdesk.mono.utils.Dp.dp40
import com.debugdesk.mono.utils.Dp.dp8
import com.debugdesk.mono.utils.Dp.dp95
import org.koin.androidx.compose.koinViewModel

@Composable
fun Graph(
    navHostController: NavHostController,
    categoryId: Int,
    graphVM: GraphVM = koinViewModel()
) {
    val graphState by graphVM.graphState.collectAsState()
    LaunchedEffect(key1 = Unit) {
        graphVM.fetchTransaction(categoryId)
    }

    GraphAnimateContainer(
        graphState = graphState,
        onIntentChanges = {
            graphVM.handleGraphIntent(it, navHostController)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphAnimateContainer(
    modifier: Modifier = Modifier,
    graphState: GraphState,
    onIntentChanges: (GraphIntent) -> Unit = {}
) {
    val buttonDisable by rememberUpdatedState(
        newValue =
        graphState.isLoading != EffectState.Loading
    )
    MonoColumn(
        modifier = modifier.fillMaxSize(),
        showBack = true,
        isScrollEnabled = false,
        headerBotPadding = dp0,
        top = dp0,
        start = dp10,
        end = dp10,
        bottom = dp10,
        heading = stringResource(id = R.string.category_report),
        headingStyle = MaterialTheme.typography.titleMedium,
        enableClick = buttonDisable,
        trailingCompose = {
            IconButton(
                onClick = { onIntentChanges(GraphIntent.PromptFilter) },
                enabled = buttonDisable
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

        },
        horizontalAlignment = Alignment.CenterHorizontally,
        onBackClick = { onIntentChanges(GraphIntent.NavigateBack) }) {
        AnimatedContent(
            targetState = graphState.isLoading,
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
            label = "AnimatedContent"
        ) {
            when (it) {
                EffectState.NoDataFound -> NoDataFoundLayout(show = true)
                EffectState.Loaded -> GraphData(
                    graphState = graphState,
                    onIntentChanges = onIntentChanges
                )

                EffectState.Loading -> Loading()

            }

        }
    }

    BottomSheet(
        show = graphState.promptFilter,
        onDismiss = { onIntentChanges(GraphIntent.HideFilter) }) {

    }
}

@Composable
private fun GraphData(
    modifier: Modifier = Modifier,
    graphState: GraphState,
    onIntentChanges: (GraphIntent) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = dp8)
    ) {
        GraphTab(
            selectedTab = graphState.selectedTabs,
            onTabSelected = { onIntentChanges(GraphIntent.UpdateTab(it)) })

        graphState.distributedTransaction.forEach { (_, transaction) ->
            ExpenseCard(
                currency = stringResource(graphState.currencyIcon),
                dailyTransaction = transaction
            )

        }
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(strokeCap = StrokeCap.Round)
    }
}

@Composable
private fun GraphTab(
    tabs: List<Tabs> = Tabs.entries,
    selectedTab: Tabs,
    onTabSelected: (Tabs) -> Unit
) {
    val selectedIndex = tabs.indexOf(selectedTab)
    val offsetX by animateDpAsState(
        targetValue = dp100 * selectedIndex,
        label = "",
        animationSpec = spring()
    )

    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(25.dp)
            )
            .border(1.dp, disableButton, RoundedCornerShape(25.dp))
            .height(dp40)
            .width(dp100 * tabs.size)
            .padding(dp3),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(25.dp)
                )
                .fillMaxHeight()
                .width(dp95)
                .padding(dp3)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(dp100)
                        .clickable { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = tab.string),
                        color = if (selectedTab == tab)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun GraphPrev() {
    PreviewTheme {
        GraphAnimateContainer(graphState = GraphState(isLoading = EffectState.Loaded))
    }
}