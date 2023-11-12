package me.paladin.barsurasp.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Outlined.Workdays: ImageVector
    get() {
        if (_workdays != null) {
            return _workdays!!
        }
        _workdays = materialIcon("Outlined.Workdays") {
            materialPath {
                moveTo(14.0f, 6.0f)
                lineTo(14.0f, 4.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(4.0f)
                close()
                moveTo(4.0f, 8.0f)
                verticalLineToRelative(11.0f)
                horizontalLineToRelative(16.0f)
                lineTo(20.0f, 8.0f)
                lineTo(4.0f, 8.0f)
                close()
                moveTo(20.0f, 6.0f)
                curveToRelative(1.11f, 0.0f, 2.0f, 0.89f, 2.0f, 2.0f)
                verticalLineToRelative(11.0f)
                curveToRelative(0.0f, 1.11f, -0.89f, 2.0f, -2.0f, 2.0f)
                lineTo(4.0f, 21.0f)
                curveToRelative(-1.11f, 0.0f, -2.0f, -0.89f, -2.0f, -2.0f)
                lineToRelative(0.01f, -11.0f)
                curveToRelative(0.0f, -1.11f, 0.88f, -2.0f, 1.99f, -2.0f)
                horizontalLineToRelative(4.0f)
                lineTo(8.0f, 4.0f)
                curveToRelative(0.0f, -1.11f, 0.89f, -2.0f, 2.0f, -2.0f)
                horizontalLineToRelative(4.0f)
                curveToRelative(1.11f, 0.0f, 2.0f, 0.89f, 2.0f, 2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(4.0f)
                close()
            }
        }
        return _workdays!!
    }

private var _workdays: ImageVector? = null
