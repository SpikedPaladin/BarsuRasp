package me.paladin.barsurasp.ui.components.barsu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.R
import me.paladin.barsurasp.ui.theme.BarsuRaspTheme

@Composable
fun GroupItem(
    group: String,
    saved: Boolean = false,
    onSaveClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Text(
            text = group,
            modifier = Modifier
                .padding(8.dp)
                .weight(1F),
            style = MaterialTheme.typography.headlineMedium
        )
        if (onSaveClick != null) {
            IconButton(onClick = onSaveClick) {
                Icon(
                    painter = if (saved) painterResource(R.drawable.ic_star)
                    else painterResource(R.drawable.ic_star_outline),
                    contentDescription = null,
                    tint = if (saved)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview
@Composable
fun GroupItemPreview() {
    BarsuRaspTheme {
        Surface {
            GroupItem(group = "TSAPP") {

            }
        }
    }
}