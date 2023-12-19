package me.paladin.barsurasp.ui.components

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CustomToolbar(
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    centralContent: (@Composable () -> Unit)? = null,
    additionalContent: (@Composable () -> Unit)? = null,
    collapsedContent: (@Composable () -> Unit)? = null,
    expandedContent: (@Composable () -> Unit)? = null,
    scrollBehavior: CustomToolbarScrollBehavior? = null,
    collapsedElevation: Dp = DefaultCollapsedElevation,
) {
    val collapsedFraction = when {
        scrollBehavior != null -> scrollBehavior.state.collapsedFraction
        else -> 1f
    }

    val centralContentAlpha = lerp(1F, 0F, collapsedFraction)

    val showElevation = when {
        scrollBehavior == null -> false
        scrollBehavior.state.contentOffset <= 0 && collapsedFraction == 1f -> true
        else -> false
    }

    val elevationState = animateDpAsState(lerp(0.dp, collapsedElevation, collapsedFraction))

    Surface(
        modifier = modifier,
        tonalElevation = elevationState.value,
        shadowElevation = elevationState.value,
    ) {
        Layout(
            content = {
                if (expandedContent != null) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .layoutId(ExpandedTitleId)
                    ) {
                        expandedContent()
                    }
                }
                if (collapsedContent != null) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .layoutId(CollapsedTitleId)
                    ) {
                        collapsedContent()
                    }
                }
                if (navigationIcon != null) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .layoutId(NavigationIconId)
                    ) {
                        navigationIcon()
                    }
                }
                if (actions != null) {
                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .layoutId(ActionsId)
                    ) {
                        actions()
                    }
                }
                if (centralContent != null) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .layoutId(CentralContentId)
                            .alpha(centralContentAlpha)
                    ) {
                        val mergedStyle =
                            LocalTextStyle.current.merge(MaterialTheme.typography.titleLarge)
                        CompositionLocalProvider(
                            LocalTextStyle provides mergedStyle,
                            content = centralContent
                        )

                    }
                }
                if (additionalContent != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .layoutId(AdditionalContentId)
                    ) {
                        additionalContent()
                    }
                }
            },
            modifier = modifier.windowInsetsPadding(
                WindowInsets
                    .systemBars
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            ).then(Modifier.heightIn(min = MinCollapsedHeight))
        ) { measurables, constraints ->
            val horizontalPaddingPx = HorizontalPadding.toPx()
            val expandedTitleBottomPaddingPx = ExpandedTitleBottomPadding.toPx()

            // Measuring widgets inside toolbar:

            val navigationIconPlaceable =
                measurables.firstOrNull { it.layoutId == NavigationIconId }
                    ?.measure(constraints.copy(minWidth = 0))

            val actionsPlaceable = measurables.firstOrNull { it.layoutId == ActionsId }
                ?.measure(constraints.copy(minWidth = 0))

            val expandedTitlePlaceable = measurables.firstOrNull { it.layoutId == ExpandedTitleId }
                ?.measure(
                    constraints.copy(
                        maxWidth = (constraints.maxWidth - 2 * horizontalPaddingPx).roundToInt(),
                        minWidth = 0,
                        minHeight = 0
                    )
                )

            val additionalContentPlaceable =
                measurables.firstOrNull { it.layoutId == AdditionalContentId }
                    ?.measure(constraints)

            val navigationIconOffset = when (navigationIconPlaceable) {
                null -> horizontalPaddingPx
                else -> navigationIconPlaceable.width + horizontalPaddingPx * 2
            }

            val actionsOffset = when (actionsPlaceable) {
                null -> horizontalPaddingPx
                else -> actionsPlaceable.width + horizontalPaddingPx * 2
            }

            val collapsedTitleMaxWidthPx =
                (constraints.maxWidth - navigationIconOffset - actionsOffset) / 1

            val collapsedTitlePlaceable =
                measurables.firstOrNull { it.layoutId == CollapsedTitleId }
                    ?.measure(
                        constraints.copy(
                            maxWidth = collapsedTitleMaxWidthPx.roundToInt(),
                            minWidth = 0,
                            minHeight = 0
                        )
                    )

            val centralContentPlaceable =
                measurables.firstOrNull { it.layoutId == CentralContentId }
                    ?.measure(
                        constraints.copy(
                            minWidth = 0,
                            maxWidth = (constraints.maxWidth - navigationIconOffset - actionsOffset).roundToInt()
                        )
                    )

            val collapsedHeightPx = MinCollapsedHeight.toPx()

            var layoutHeightPx = collapsedHeightPx


            // Calculating coordinates of widgets inside toolbar:

            // Current coordinates of navigation icon
            val navigationIconX = horizontalPaddingPx.roundToInt()
            val navigationIconY =
                ((collapsedHeightPx - (navigationIconPlaceable?.height ?: 0)) / 2).roundToInt()

            // Current coordinates of actions
            val actionsX = (constraints.maxWidth - (actionsPlaceable?.width
                ?: 0) - horizontalPaddingPx).roundToInt()
            val actionsY = ((collapsedHeightPx - (actionsPlaceable?.height ?: 0)) / 2).roundToInt()

            // Current coordinates of title
            var collapsingTitleY = 0
            var collapsingTitleX = 0

            if (expandedTitlePlaceable != null && collapsedTitlePlaceable != null) {
                // Measuring toolbar collapsing distance
                val heightOffsetLimitPx =
                    expandedTitlePlaceable.height + expandedTitleBottomPaddingPx
                scrollBehavior?.state?.heightOffsetLimit = -heightOffsetLimitPx

                // Toolbar height at fully expanded state
                val fullyExpandedHeightPx = MinCollapsedHeight.toPx() + heightOffsetLimitPx

                // Coordinates of fully expanded title
                val fullyExpandedTitleY =
                    fullyExpandedHeightPx - expandedTitlePlaceable.height - expandedTitleBottomPaddingPx

                // Coordinates of fully collapsed title
                val fullyCollapsedTitleY =
                    collapsedHeightPx / 2 - (collapsedTitlePlaceable.height / 2)
                //    collapsedHeightPx / 2 - CollapsedTitleLineHeight.toPx().roundToInt() / 2

                // Current height of toolbar
                layoutHeightPx = lerp(fullyExpandedHeightPx, collapsedHeightPx, collapsedFraction)

                // Current coordinates of collapsing title
                collapsingTitleX =
                    lerp(horizontalPaddingPx, navigationIconOffset, collapsedFraction).roundToInt()
                collapsingTitleY =
                    lerp(fullyExpandedTitleY, fullyCollapsedTitleY, collapsedFraction).roundToInt()
            } else {
                scrollBehavior?.state?.heightOffsetLimit = -1f
            }

            val toolbarHeightPx =
                layoutHeightPx.roundToInt() + (additionalContentPlaceable?.height ?: 0)


            // Placing toolbar widgets:

            layout(constraints.maxWidth, toolbarHeightPx) {
                navigationIconPlaceable?.placeRelative(
                    x = navigationIconX,
                    y = navigationIconY
                )
                actionsPlaceable?.placeRelative(
                    x = actionsX,
                    y = actionsY
                )
                centralContentPlaceable?.placeRelative(
                    x = navigationIconOffset.roundToInt(),
                    y = ((collapsedHeightPx - centralContentPlaceable.height) / 2).roundToInt()
                )
                if (expandedTitlePlaceable?.width == collapsedTitlePlaceable?.width) {
                    expandedTitlePlaceable?.placeRelative(
                        x = collapsingTitleX,
                        y = collapsingTitleY,
                    )
                } else {
                    expandedTitlePlaceable?.placeRelativeWithLayer(
                        x = collapsingTitleX,
                        y = collapsingTitleY,
                        layerBlock = { alpha = 1 - collapsedFraction }
                    )
                    collapsedTitlePlaceable?.placeRelativeWithLayer(
                        x = collapsingTitleX,
                        y = collapsingTitleY,
                        layerBlock = { alpha = collapsedFraction }
                    )
                }
                additionalContentPlaceable?.placeRelative(
                    x = 0,
                    y = layoutHeightPx.roundToInt()
                )
            }
        }

    }
}

private val MinCollapsedHeight = 64.dp
private val HorizontalPadding = 4.dp
private val ExpandedTitleBottomPadding = 8.dp
private val DefaultCollapsedElevation = 4.dp

private const val ExpandedTitleId = "expandedTitle"
private const val CollapsedTitleId = "collapsedTitle"
private const val NavigationIconId = "navigationIcon"
private const val ActionsId = "actions"
private const val CentralContentId = "centralContent"
private const val AdditionalContentId = "additionalContent"


/**
 * A state object that can be hoisted to control and observe the top app bar state. The state is
 * read and updated by a [CustomToolbarScrollBehavior] implementation.
 *
 * In most cases, this state will be created via [rememberToolbarScrollBehavior].
 *
 * @param initialHeightOffsetLimit the initial value for [CustomToolbarScrollState.heightOffsetLimit]
 * @param initialHeightOffset the initial value for [CustomToolbarScrollState.heightOffset]
 * @param initialContentOffset the initial value for [CustomToolbarScrollState.contentOffset]
 */
@Stable
class CustomToolbarScrollState(
    initialHeightOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float,
) {
    companion object {
        val Saver: Saver<CustomToolbarScrollState, *> = listSaver(
            save = { listOf(it.heightOffsetLimit, it.heightOffset, it.contentOffset) },
            restore = {
                CustomToolbarScrollState(
                    initialHeightOffsetLimit = it[0],
                    initialHeightOffset = it[1],
                    initialContentOffset = it[2]
                )
            }
        )
    }

    /**
     * The top app bar's height offset limit in pixels, which represents the limit that a top app
     * bar is allowed to collapse to.
     *
     * Use this limit to coerce the [heightOffset] value when it's updated.
     */
    var heightOffsetLimit by mutableStateOf(initialHeightOffsetLimit)

    /**
     * The top app bar's current height offset in pixels. This height offset is applied to the fixed
     * height of the app bar to control the displayed height when content is being scrolled.
     *
     * Updates to the [heightOffset] value are coerced between zero and [heightOffsetLimit].
     */
    var heightOffset: Float
        get() = _heightOffset.value
        set(newOffset) {
            _heightOffset.value = newOffset.coerceIn(
                minimumValue = heightOffsetLimit,
                maximumValue = 0f
            )
        }

    /**
     * The total offset of the content scrolled under the top app bar.
     *
     * This value is updated by a [CustomToolbarScrollBehavior] whenever a nested scroll connection
     * consumes scroll events. A common implementation would update the value to be the sum of all
     * [NestedScrollConnection.onPostScroll] `consumed.y` values.
     */
    var contentOffset by mutableStateOf(initialContentOffset)

    /**
     * A value that represents the collapsed height percentage of the app bar.
     *
     * A `0.0` represents a fully expanded bar, and `1.0` represents a fully collapsed bar (computed
     * as [heightOffset] / [heightOffsetLimit]).
     */
    val collapsedFraction: Float
        get() = if (heightOffsetLimit != 0f) {
            heightOffset / heightOffsetLimit
        } else {
            0f
        }

    private var _heightOffset = mutableStateOf(initialHeightOffset)

}

@Composable
internal fun rememberToolbarScrollState(
    initialHeightOffsetLimit: Float = -Float.MAX_VALUE,
    initialHeightOffset: Float = 0f,
    initialContentOffset: Float = 0f
) = rememberSaveable(saver = CustomToolbarScrollState.Saver) {
    CustomToolbarScrollState(
        initialHeightOffsetLimit,
        initialHeightOffset,
        initialContentOffset
    )
}


class CustomToolbarScrollBehavior(
    val state: CustomToolbarScrollState,
    val flingAnimationSpec: DecayAnimationSpec<Float>?,
) {

    val nestedScrollConnection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            // Don't intercept if scrolling down.
            if (available.y > 0f) return Offset.Zero

            val prevHeightOffset = state.heightOffset
            state.heightOffset += available.y
            return if (prevHeightOffset != state.heightOffset) {
                // We're in the middle of top app bar collapse or expand.
                // Consume only the scroll on the Y axis.
                available.copy(x = 0f)
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            state.contentOffset += consumed.y

            if (available.y < 0f || consumed.y < 0f) {
                // When scrolling up, just update the state's height offset.
                val oldHeightOffset = state.heightOffset
                state.heightOffset += consumed.y
                return Offset(0f, state.heightOffset - oldHeightOffset)
            }

            if (consumed.y == 0f && available.y > 0) {
                // Reset the total content offset to zero when scrolling all the way down. This
                // will eliminate some float precision inaccuracies.
                state.contentOffset = 0f
            }

            if (available.y > 0f) {
                // Adjust the height offset in case the consumed delta Y is less than what was
                // recorded as available delta Y in the pre-scroll.
                val oldHeightOffset = state.heightOffset
                state.heightOffset += available.y
                return Offset(0f, state.heightOffset - oldHeightOffset)
            }
            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            var result = super.onPostFling(consumed, available)
            // Check if the app bar is partially collapsed/expanded.
            // Note that we don't check for 0f due to float precision with the collapsedFraction
            // calculation.
            if (state.collapsedFraction > 0.01f && state.collapsedFraction < 1f) {
                result += flingToolbar(
                    state = state,
                    initialVelocity = available.y,
                    flingAnimationSpec = flingAnimationSpec
                )
                snapToolbar(state)
            }
            return result
        }

    }

}

private suspend fun flingToolbar(
    state: CustomToolbarScrollState,
    initialVelocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
): Velocity {
    var remainingVelocity = initialVelocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(initialVelocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = initialVelocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) {
                    cancelAnimation()
                }
            }
    }
    return Velocity(0f, remainingVelocity)
}

private suspend fun snapToolbar(state: CustomToolbarScrollState) {
    // In case the app bar motion was stopped in a state where it's partially visible, snap it to
    // the nearest state.
    if (state.heightOffset < 0 &&
        state.heightOffset > state.heightOffsetLimit
    ) {
        AnimationState(
            initialValue = state.heightOffset
        ).animateTo(
            targetValue = if (state.collapsedFraction < 0.5f) 0f else state.heightOffsetLimit,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ) {
            state.heightOffset = value
        }
    }
}

@Composable
fun rememberToolbarScrollBehavior() = CustomToolbarScrollBehavior(
    state = rememberToolbarScrollState(
        initialHeightOffsetLimit = -Float.MAX_VALUE
    ),
    flingAnimationSpec = rememberSplineBasedDecay()
)