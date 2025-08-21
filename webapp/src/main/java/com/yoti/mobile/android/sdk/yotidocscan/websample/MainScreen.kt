package com.yoti.mobile.android.sdk.yotidocscan.websample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yoti.mobile.android.sdk.yotidocscan.websample.ui.YotiDocScanWebSampleAppTheme

@Composable
fun MainScreen(
        sessionUrl: String,
        showMissingPermissionsDialog: Boolean,
        onSessionUrlChanged: (String) -> Unit,
        onStartSessionClicked: () -> Unit,
        onMissingPermissionsConfirmed: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
                value = sessionUrl,
                onValueChange = onSessionUrlChanged,
                label = {
                    Text(text = stringResource(id = R.string.session_url_hint))
                },
                keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(16.dp))
        Button(
                onClick = onStartSessionClicked,
                enabled = sessionUrl.isNotBlank()
        ) {
            Text(text = stringResource(id = R.string.start_session_button))
        }

        if (showMissingPermissionsDialog) {
            MissingPermissionsDialog { onMissingPermissionsConfirmed() }
        }
    }
}

@Composable
private fun MissingPermissionsDialog(onConfirm: () -> Unit) {
    AlertDialog(
            title = { Text(stringResource(id = R.string.missing_permissions_dialog_title)) },
            text = { Text(stringResource(id = R.string.missing_permissions_dialog_text)) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(id = R.string.missing_permissions_dialog_confirm_button))
                }
            },
            onDismissRequest = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyMainScreen() {
    YotiDocScanWebSampleAppTheme {
        MainScreen(
                sessionUrl = "",
                showMissingPermissionsDialog = false,
                onSessionUrlChanged = {},
                onStartSessionClicked = {},
                onMissingPermissionsConfirmed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    YotiDocScanWebSampleAppTheme {
        MainScreen(
                sessionUrl = "https://example.com/session",
                showMissingPermissionsDialog = false,
                onSessionUrlChanged = {},
                onStartSessionClicked = {},
                onMissingPermissionsConfirmed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenWithMissingPermissionsDialog() {
    YotiDocScanWebSampleAppTheme {
        MainScreen(
                sessionUrl = "",
                showMissingPermissionsDialog = true,
                onSessionUrlChanged = {},
                onStartSessionClicked = {},
                onMissingPermissionsConfirmed = {}
        )
    }
}