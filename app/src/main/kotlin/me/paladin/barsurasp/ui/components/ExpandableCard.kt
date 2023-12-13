package me.paladin.barsurasp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableCard(
    title: String,
    outlined: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val transition = updateTransition(expanded, label = "transition")

    val paddingVertical by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "paddingHorizontalTransition"
    ) { isExpanded ->
        if (isExpanded) 4.dp else 0.dp
    }

    val paddingHorizontal by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "paddingHorizontalTransition"
    ) { isExpanded ->
        if (isExpanded) 8.dp else 0.dp
    }

    val cardElevation by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "cardElevationTransition"
    ) { isExpanded ->
        if (isExpanded) 4.dp else 0.dp
    }

    if (outlined)
        OutlinedCard(
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 4.dp,
                    horizontal = paddingHorizontal
                ),
            onClick = { expanded = !expanded }
        ) {
            Column {
                Box {
                    CardTitle(title = title)
                }
                ExpandableContent(expanded, content)
            }
        }
    else
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = cardElevation
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = paddingVertical,
                    horizontal = paddingHorizontal
                ),
            onClick = { expanded = !expanded }
        ) {
            Column {
                Box {
                    CardTitle(title = title)
                }
                ExpandableContent(expanded, content)
            }
        }
}

@Composable
private fun CardTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Center
    )
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
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            HorizontalDivider()
            Column(content = content)
        }
    }
}