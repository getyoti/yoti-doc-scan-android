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