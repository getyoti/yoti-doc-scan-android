package com.yoti.mobile.android.sdk.yotidocscan.sample.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun YotiDocScanSampleAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
            colorScheme = lightColorScheme(),
            content = content
    )
}