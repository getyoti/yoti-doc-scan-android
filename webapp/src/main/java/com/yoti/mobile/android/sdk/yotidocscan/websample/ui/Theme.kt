package com.yoti.mobile.android.sdk.yotidocscan.websample.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun YotiDocScanWebSampleAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
            colorScheme = lightColorScheme(),
            content = content
    )
}