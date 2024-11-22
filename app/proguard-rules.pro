-keepclassmembers class kotlinx.** {
    volatile <fields>;
}
-keep class com.yoti.** { *; }
-keep class com.microblink.** { *; }
-keep class com.microblink.**$* { *; }
-dontwarn com.microblink.**
-dontwarn com.facetec.sdk.**
-keep class com.facetec.sdk.** { *; }
-dontwarn javax.annotation.Nullable
-keepclassmembers class io.ktor.** { volatile <fields>; }


# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.yoti.mobile.android.scan.ScannerViewBaseFragment$Companion
-dontwarn com.yoti.mobile.documentscanconfig.DocumentScanConfig
-dontwarn kotlinx.parcelize.Parcelize
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.slf4j.impl.StaticLoggerBinder



-keep class com.haroldadmin.cnradapter.** { *; }