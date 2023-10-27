package me.paladin.barsurasp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
fun AdminBottomSheet(
    doneCallback: () -> Unit,
    hideCallback: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var input by remember { mutableStateOf("") }
    var completeFirst by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = hideCallback,
        sheetState = sheetState
    ) {
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
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            doneCallback()
                            hideCallback()
                        }
                    }

                input = ""
            }) {
                Text(text = "Непон?")
            }
        }
    }
}