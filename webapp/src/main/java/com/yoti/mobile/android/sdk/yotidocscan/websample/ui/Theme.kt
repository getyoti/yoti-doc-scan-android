package com.yoti.mobile.android.sdk.yotidocscan.websample.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
        background = Color.White
)

@Composable
fun YotiDocScanWebSampleAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
            colorScheme = LightColorScheme,
            content = content
    )
}