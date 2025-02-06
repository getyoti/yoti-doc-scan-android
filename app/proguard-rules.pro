-keepclassmembers class kotlinx.** {
    volatile <fields>;
}
-keep class com.yoti.** { *; }
-dontwarn com.facetec.sdk.**
-keep class com.facetec.sdk.** { *; }
-dontwarn javax.annotation.Nullable
-keepclassmembers class io.ktor.** { volatile <fields>; }