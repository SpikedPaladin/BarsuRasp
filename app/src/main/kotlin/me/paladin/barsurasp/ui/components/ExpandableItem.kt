package me.paladin.barsurasp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ln

@Composable
fun ExpandableItem(
    expanded: Boolean,
    onClick: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val transition = updateTransition(expanded, label = "transition")

    val color by transition.animateColor(
        transitionSpec = { tween(300) },
        label = "colorTransition"
    ) { isExpanded ->
        if (isExpanded) surfaceColorAtElevation(
            color = MaterialTheme.colorScheme.surface,
            elevation = 4.dp
        ) else MaterialTheme.colorScheme.surface
    }

    val bottomPadding by transition.animateDp(
        transitionSpec = { tween(300) },
        label = "bottomPaddingTransition"
    ) { isExpanded ->
        if (isExpanded) 8.dp
        else 0.dp
    }

    Column {
        Surface(
            color = color,
            shadowElevation = bottomPadding / 2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = bottomPadding)
                .clickable(onClick = onClick)
        ) { title() }
        ExpandableContent(expanded, content)
    }
}

@Composable
private fun ExpandableContent(
    visible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(450)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(450)
        )
    }

    val exitTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(450)
        ) + fadeOut(
            animationSpec = tween(450)
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterTransition,
        exit = exitTransition
    ) { Column(content = content) }
}

@Composable
private fun surfaceColorAtElevation(
    color: Color,
    elevation: Dp
): Color {
    if (elevation == 0.dp) return color
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return MaterialTheme.colorScheme.surfaceTint.copy(alpha = alpha).compositeOver(color)
}