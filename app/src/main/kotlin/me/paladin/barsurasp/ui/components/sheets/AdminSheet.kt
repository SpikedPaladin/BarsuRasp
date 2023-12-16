package me.paladin.barsurasp.ui.components.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun AdminSheet(
    visible: Boolean,
    onAccess: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalSheet(visible = visible, onDismiss = onDismiss) { sheetState ->
        var completeFirst by remember { mutableStateOf(false) }
        var input by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Кто я? Зачем ты здесь?")
            OutlinedTextField(value = input, onValueChange = { input = it })
            Button(onClick = {
                if (input == "PaladinDev")
                    completeFirst = true

                if (completeFirst && input == "=-=-=-=-")
                    scope
                        .launch { sheetState.hide() }
                        .invokeOnCompletion {
                            onAccess()
                            onDismiss()
                        }

                input = ""
            }) {
                Text(text = "Непон?")
            }
        }
    }
}