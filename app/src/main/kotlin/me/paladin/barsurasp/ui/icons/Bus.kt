package me.paladin.barsurasp.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Outlined.Bus: ImageVector
    get() {
        if (_bus != null) {
            return _bus!!
        }
        _bus = materialIcon("Filled.Bus") {
            materialPath {
                moveTo(12.0f, 2.0f)
                curveToRelative(-4.42f, 0.0f, -8.0f, 0.5f, -8.0f, 4.0f)
                verticalLineToRelative(10.0f)
                curveToRelative(0.0f, 0.88f, 0.39f, 1.67f, 1.0f, 2.22f)
                lineTo(5.0f, 20.0f)
                curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
                horizontalLineToRelative(1.0f)
                curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
                verticalLineToRelative(-1.0f)
                horizontalLineToRelative(8.0f)
                verticalLineToRelative(1.0f)
                curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
                horizontalLineToRelative(1.0f)
                curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
                verticalLineToRelative(-1.78f)
                curveToRelative(0.61f, -0.55f, 1.0f, -1.34f, 1.0f, -2.22f)
                lineTo(20.0f, 6.0f)
                curveToRelative(0.0f, -3.5f, -3.58f, -4.0f, -8.0f, -4.0f)
                close()
                moveTo(17.66f, 4.99f)
                lineTo(6.34f, 4.99f)
                curveTo(6.89f, 4.46f, 8.31f, 4.0f, 12.0f, 4.0f)
                reflectiveCurveToRelative(5.11f, 0.46f, 5.66f, 0.99f)
                close()
                moveTo(18.0f, 6.99f)
                lineTo(18.0f, 10.0f)
                lineTo(6.0f, 10.0f)
                lineTo(6.0f, 6.99f)
                horizontalLineToRelative(12.0f)
                close()
                moveTo(17.66f, 16.73f)
                lineToRelative(-0.29f, 0.27f)
                lineTo(6.63f, 17.0f)
                lineToRelative(-0.29f, -0.27f)
                curveTo(6.21f, 16.62f, 6.0f, 16.37f, 6.0f, 16.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineToRelative(12.0f)
                verticalLineToRelative(4.0f)
                curveToRelative(0.0f, 0.37f, -0.21f, 0.62f, -0.34f, 0.73f)
                close()
            }
            materialPath {
                moveTo(8.5f, 14.5f)
                moveToRelative(-1.5f, 0.0f)
                arcToRelative(1.5f, 1.5f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 3.0f,
                    dy1 = 0.0f
                )
                arcToRelative(1.5f, 1.5f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -3.0f,
                    dy1 = 0.0f
                )
            }
            materialPath {
                moveTo(15.5f, 14.5f)
                moveToRelative(-1.5f, 0.0f)
                arcToRelative(1.5f, 1.5f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 3.0f,
                    dy1 = 0.0f
                )
                arcToRelative(1.5f, 1.5f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -3.0f,
                    dy1 = 0.0f
                )
            }
        }
        return _bus!!
    }

private var _bus: ImageVector? = null
