package com.yoti.mobile.android.sdk.yotidocscan.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
        sessionId: String,
        onSessionIdChanged: (String) -> Unit,
        sessionToken: String,
        onSessionTokenChanged: (String) -> Unit,
        sessionStatus: String,
        onScanDocumentClicked: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
                value = sessionId,
                onValueChange = onSessionIdChanged,
                label = {
                    Text(text = stringResource(id = R.string.session_id_hint))
                },
                keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
                value = sessionToken,
                onValueChange = onSessionTokenChanged,
                label = {
                    Text(text = stringResource(id = R.string.session_token_hint))
                },
                keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
        )
        Text(
                text = sessionStatus,
                fontSize = 16.sp
        )
        Spacer(Modifier.weight(1f))
        Button(
                onClick = onScanDocumentClicked,
                modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = stringResource(R.string.scan_document_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyMainScreen() {
    MaterialTheme {
        MainScreen(
                sessionId = "",
                onSessionIdChanged = {},
                sessionToken = "",
                onSessionTokenChanged = {},
                sessionStatus = "",
                onScanDocumentClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MaterialTheme {
        MainScreen(
                sessionId = "12345678912345678912345",
                onSessionIdChanged = {},
                sessionToken = "abcdefghijklmnopqrstuvwxyz",
                onSessionTokenChanged = {},
                sessionStatus = "Session status: 1000",
                onScanDocumentClicked = {}
        )
    }
}