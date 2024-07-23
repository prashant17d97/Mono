package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.utils.CommonColor.brandColor
import com.debugdesk.mono.utils.Dp.dp4
import com.debugdesk.mono.utils.Dp.dp40
import com.debugdesk.mono.utils.Dp.dp80

@Composable
fun CategoryCard(
    model: CategoryModel,
    selectedColor: Color = brandColor,
    unSelectedColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    onClick: (CategoryModel) -> Unit = { _ -> },
) {
    val color by animateColorAsState(
        targetValue =
        selectedColor.takeIf { model.isSelected }
            ?: unSelectedColor,
        label = "",
    )

    val alpha by animateFloatAsState(targetValue = if (model.enable) 1f else 0.5f, label = "Alpha")
    Column(
        modifier =
        Modifier
            .clickable {
                if (model.enable) {
                    onClick(model.copy(isSelected = !model.isSelected))
                }
            }
            .size(dp80)
            .padding(dp4)
            .border(
                width = 1.dp,
                color = color.copy(alpha = alpha),
                shape = RoundedCornerShape(7.dp),
            ),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (model.categoryIcon != null) {
            Image(
                painter = painterResource(id = model.categoryIcon),
                contentDescription = "categoryIcon",
                contentScale = ContentScale.Inside,
                modifier = Modifier.size(dp40),
                colorFilter = ColorFilter.tint(color.copy(alpha = alpha)),
            )
        }
        Text(
            text = model.category,
            style = MaterialTheme.typography.titleSmall.copy(color = color.copy(alpha = alpha)),
        )
    }
}

@Preview
@Composable
fun CategoryPrev() {
    PreviewTheme(isDarkTheme = true) {
        CategoryCard(
            model =
            CategoryModel(
                category = stringResource(id = R.string.coffee),
                categoryIcon = R.drawable.ic_trash,
                isSelected = false,
            ),
        )
    }
}

@Preview
@Composable
fun CategoryPrev2() {
    PreviewTheme(isDarkTheme = true) {
        CategoryCard(
            model =
            CategoryModel(
                category = stringResource(id = R.string.coffee),
                categoryIcon = R.drawable.ic_trash,
                isSelected = true,
            ),
        )
    }
}
