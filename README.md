![YotiBanner](./yoti_banner.png)

[![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/getyoti/yoti-doc-scan-android?label=latest%20release)](https://github.com/getyoti/yoti-doc-scan-android/releases)

## Yoti Doc Scan - Android SDK

Yoti is an identity checking platform that allows organisations to verify who people are, online and in person.
The Yoti SDK, allows the user to take a photo of their ID, we then verify this instantly and prepare a response, which your system can then retrieve on your hosted site.
Further information can be found [here](https://developers.yoti.com/identity-verification)

## Table of Contents
- [Requirements](#requirements)
- [Set up the SDK](#set-up-the-sdk)
    - [R8 and ProGuard](#r8-and-proguard)
- [Start the SDK](#start-the-sdk)
- [Retrieve status of the session](#retrieve-status-of-the-session)
    - [Alternative for apps using Jetpack Compose](#alternative-for-apps-using-jetpack-compose)
    - [Possible status for the session](#possible-status-for-the-session)
- [Supported languages](#supported-languages)
- [Customisation](#customisation)
    - [Font type](#font-type)
    - [Font Colour](#font-colour)
    - [Colours](#colours)
- [SDK size](#sdk-size)
    - [Individual modules](#individual-modules)
    - [Module combinations](#module-combinations)
- [Reducing the size of your APK](#reducing-the-size-of-your-apk)
- [Troubleshooting](#troubleshooting)
    - [Google Play automatic protection](#google-play-automatic-protection)
- [Support](#support)
- [License](#license)

## Requirements
You have setup the Yoti Doc Scan SDK on your backend, you can find the documentation [here](https://developers.yoti.com/identity-verification/getting-started)

Minimum Android API level supported: 21.

Note: we are using libraries from Android Jetpack. If you are still using the original Android Support Libraries you may encounter some issues when trying to use our SDK.
We strongly recommend you to migrate your app to the new Androidx libraries: https://developer.android.com/jetpack/androidx/migrate

## Set up the SDK

Make sure you have the mavenCentral repository added in your allProjects entry in your root build.gradle file. If you are using the `yoti-sdk-doc-scan` module, you will also need to add the regulaforensics repository:

```groovy
allprojects {
    repositories {
        mavenCentral()
        maven { url "https://maven.regulaforensics.com/RegulaDocumentReader" }
        maven { url "https://jitpack.io" }
        ...
    }
    ...
}
```

Note: If instead you are using the `yoti-sdk-doc-scan-slim` module, you do not need to add the regulaforensics repository.

If you are still using Yoti SDK version `3.5.2` or below, please refer to [these instructions](https://github.com/getyoti/yoti-doc-scan-android/blob/v3.5.2/README.md#setup-the-sdk) instead, but keep in mind that support for older versions is not guaranteed and we highly recommend updating to the latest one as soon as possible.

The Yoti SDK is composed of multiple feature modules. Each feature is optional, but you must include at least one to use the SDK.

The modules you include must match those requested by your backend. Attempts to use a module you haven't included will fail at runtime with a [600x response code](#possible-status-for-the-session).

Add modules you require to your build.gradle:
```groovy
dependencies {
    //If you need document capture
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-doc-scan:4.4.0'
    //Or if you want the version without OCR and NFC capture, which is ~20 MB smaller in size
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-doc-scan-slim:4.4.0'

    //If you need supplementary documents
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-doc-scan-sup:4.4.0'

    //If you need liveness
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-liveness-zoom:4.4.0'

    //If you need selfie capture
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-facecapture:4.4.0'
    //Or if you want the version without an embedded AI model, which is ~7 MB smaller in size
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-facecapture-unbundled:4.4.0'
}
```

As you can see above, there are two options to add the `doc-scan` module to your app:
- `yoti-sdk-doc-scan` standard ID doc capture flow which uses automatic document capture via OCR and NFC capture for NFC-chip supported documents such as passports
- `yoti-sdk-doc-scan-slim` ID doc capture flow with a reduced feature set, using manual capture without OCR and NFC support. This results in a significantly smaller APK and install size (see [SDK size](#sdk-size)).

There are also two options to add the `facecapture` module to your app:
- `yoti-sdk-facecapture` embeds an AI model for face detection.
- `yoti-sdk-facecapture-unbundled` will manage the download of the AI model via Google Play Services the first time you start using the AI model and thus is ~7 MB smaller in size. Additionally, you can add the following metadata to your `AndroidManifest.xml` to get the model downloaded as soon as the app is installed:
```
<application ...>
  ...
  <meta-data
      android:name="com.google.firebase.ml.vision.DEPENDENCIES"
      android:value="face" />

</application>
```

Also you will need to add the following to your app-level build.gradle file, inside your Android block:

```groovy
    packagingOptions {
        exclude 'META-INF/*.kotlin_module'
        exclude "**/kotlin/**"
    }

    compileOptions {
        sourceCompatibility = '1.8'
        targetCompatibility = '1.8'
    }
```

And if you're using [Firebase performance gradle Plugin](https://firebase.google.com/docs/perf-mon/disable-sdk?platform=android), you'll need to disable it for debug build variant:
```groovy
    android {
        ...
        buildTypes {
            ...
            debug {
                ...
                FirebasePerformance {
                    instrumentationEnabled false
                }
            }
         }
    }
```

To further decrease the size footprint of the SDK, you can also opt to manually exclude some of the educational videos shown in the verification flows:
- `yds_aadhaar_educational` (~750Kb) - only exclude if you do not support the Indian Aadhaar as a valid ID document type.
- `yds_liveness_educational` (~560Kb) - only exclude if you're using the `yoti-sdk-liveness-zoom` dependency.

**Note:** It's important to mention that excluding these resources is not recommended and should be only done in case the conditions described above apply to
your use case. Otherwise, the user experience would be impacted on the related screens which will not be showing anything in place of the missing video resources. 

To exclude these video resources during build time you have to: 
1. Set `shrinkResources true` for your build type in your app `build.gradle` file. This should already be the case if you're concerned about app size.
2. Create a `keep.xml` file in your app module's `res/raw` resource directory, where you specify which resources you want to exclude:
```
<resources xmlns:tools="http://schemas.android.com/tools"
    tools:discard="@raw/yds_aadhaar_educational,@raw/yds_liveness_educational" />
```

For more information on resource shrinking and excluding resources, check out the [official documentation](https://developer.android.com/build/shrink-code#keep-resources).

### R8 and ProGuard

If you are using R8 the shrinking and obfuscation rules are included automatically.

ProGuard users must manually add the options from [proguard-rules.pro](https://github.com/getyoti/yoti-doc-scan-android/blob/master/app/proguard-rules.pro). 
You might also need rules for [retrofit](https://github.com/square/retrofit/blob/5c6620/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro), [OkHttp](https://github.com/square/okhttp/blob/a16ec15/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro), [Okio](https://github.com/square/okio/blob/f906821e6/okio/src/jvmMain/resources/META-INF/proguard/okio.pro) and [Gson](https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg) which are dependencies of this library.

## Start the SDK

After creating the session on your backend you will get a session ID and a session token, you will need to provide them to your app.
Then you can start the SDK like this:

```kotlin
class MainActivity : AppCompatActivity() {

    private val yotiSdk = YotiSdk(this)
    private val yotiSdkLauncher = registerForActivityResult(YotiSdkContract()) { onYotiSdkResult() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        startButton.setOnClickListener {
            yotiSdk
                    .setSessionId("<Your Session ID>")
                    .setClientSessionToken("<Your Session Token>")
                    .start(yotiSdkLauncher)
        }
    }

    // ... other code ...
  
}
```

## Retrieve status of the session

Once the user completed the flow in the SDK, the user will be redirected to the Activity which started the SDK.
You can then retrieve the current status of the current session like that:

```kotlin
class MainActivity : AppCompatActivity() {

    private val yotiSdk = YotiSdk(this)
  
    // ... other code ...

    private fun onYotiSdkResult() {
        val sessionStatusCode = yotiSdk.sessionStatusCode
        val sessionStatusDescription = yotiSdk.sessionStatusDescription

        result_text.text = "Result code: $sessionStatusCode Message: $sessionStatusDescription"
    }
}
```

### Alternative for apps using Jetpack Compose

If instead of the legacy View system you are using Jetpack Compose, you can create the `ActivityResultLauncher`
inside your `@Composable` like this:

```kotlin
val launcher = rememberLauncherForActivityResult(contract = YotiSdkContract()) {
    onYotiSdkResult()
}
```

And then pass the `launcher` to the SDK in the same manner as shown above.
See `MainActivity.kt` from the sample `app` module for a complete example.

### Possible status for the session

| Code              | Message                      | Retry possible for the same session                    |
| ----------------- | ---------------------------- | ---------------------------------- |
| 0                 | Result with success          | No                                 |
| 1000              | No error occurred - the end-user cancelled the session for an unknown reason           | Yes |
| 2000              | Unauthorised request (wrong or expired session token)           | Yes |
| 2001              | Session not found           | Yes |
| 2003              | SDK launched without session Token           | Yes |
| 2004              | SDK launched without session ID           | Yes |
| 3000              | Yoti's services are down or unable to process the request           | Yes |
| 3001              | An error occurred during a network request          | Yes |
| 3002              | User has no network          | Yes |
| 3003              | A timeout occurred during a network request          | Yes |
| 4000              | The user did not grant permissions to the camera          | Yes |
| 4001              | User Wrong submission          | Yes |
| 5000              | No camera (when user's camera was not found and file upload is not allowed)          | No |
| 5002              | No more local tries for the liveness flow          | Yes |
| 5003              | SDK is out-of-date - please update the SDK to the latest version          | No |
| 5004              | Unexpected internal error          | No |
| 5005              | Unexpected document scanning error          | No |
| 5006              | Unexpected liveness error          | No |
| 5008              | Unsupported configuration          | No |
| 5009              | Storage Error: could not read/write on device app cache          | No |
| 6000              | Document Capture dependency not found error          | No |
| 6001              | Liveness Zoom dependency not found error          | No |
| 6002              | Supplementary document dependency not found error          | No |
| 6003              | Face Capture dependency not found error          | No |
| 7000              | The user does not have the required documents to complete the session          | No |


## Supported languages
Our SDK supports the 9 languages listed in the table below:

Language | Code
:-- | :--
Arabic | ar
Dutch | nl
English (default) | en
French | fr
German | de
Italian | it
Russian | ru
Spanish | es
Turkish | tr

The default language we use is English, meaning that if your app supports any extra languages matching the one from the phone's settings, the SDK will fallback to English.

Since we also support Arabic, which is a right-to-left (RTL) language, you will need to add the `android:supportsRtl="true"` attribute to your `application` inside your `AndroidManifest.xml` file to ensure that the layout and text direction are correct, regardless of what languages your app supports.

If your app does not support one or more languages from the above table and the phone is set to such a language, in order to avoid situations where our SDK would be shown in a different language than the one your app is using, you need to declare the languages your app supports. You can achieve this by adding the following to your `app/build.gradle` file:
```
android {
    defaultConfig {
        resConfigs "en", "es", "it" // order does not matter, just add all your supported languages here
    }
}
```
Apart from helping you avoid issues such as the one outlined above, this will strip away all language related resources except for those in the specified list. Thus, you avoid resource contamination and it makes your app smaller in size.

## Customisation
You can customise the appearance of the screens of the SDK by overriding some of the colours.

### Font type
In order to change the font type you need to:
1. Add your own font type .tff files to res/font, 3 files in total for bold, regular and medium
2. Declare three resource items of type font (which will override the ones declared in the SDK):
```
<item name="yoti_sdk_fontStyleBold" type="font">@font/your-font-bold</item>
<item name="yoti_sdk_fontStyleRegular" type="font">@font/your-font-regular</item>
<item name="yoti_sdk_fontStyleMedium" type="font">>@font/your-font-medium</item>
```

### Font Colour
In order to change the font colour you just need to override the following colour:
```xml
<color name="yoti_sdk_colorFont">
```

### Colours
In order to change the colours of the different elements of the screens you just need to declare the following colours:

- For the main app branding colour:
```xml
<color name="yoti_sdk_colorPrimary">
```

- For the darker variant:
```xml
<color name="yoti_sdk_colorPrimaryDark">
```

- For the UI controls like checkboxes and text fields:
```xml
<color name="yoti_sdk_colorAccent">
<color name="yoti_sdk_colorAccentPressed">#AA164A</color>
<color name="yoti_sdk_colorAccentDisabled">#F8B3CB</color>
```
## SDK size

The tables below show approximately how much each SDK module adds to your app's **download size** from Google Play.

When you publish an [App Bundle](https://developer.android.com/guide/app-bundle), Google Play generates optimized APKs for each device. Users only download the native code for their device's CPU architecture, plus the resources for their screen density and language.

Some SDK modules include native libraries, so their size depends on the architecture: `arm64-v8a` is used by most current devices, and `armeabi-v7a` by older or entry-level 32-bit devices. The tables only list `armeabi-v7a` and `arm64-v8a`; `x86` and `x86_64` are mainly used by emulators and some ChromeOS devices, so they are not that relevant here.

### Individual modules

| Module | Feature | armeabi-v7a | arm64-v8a |
|--------|---------|------------:|----------:|
| `yoti-sdk-doc-scan` | ID documents | 23.0 MB | 25.2 MB |
| `yoti-sdk-doc-scan-slim` | ID documents (without OCR and NFC) | 3.5 MB | 3.6 MB |
| `yoti-sdk-doc-scan-sup` | Supplementary documents | 2.4 MB | 2.4 MB |
| `yoti-sdk-liveness-zoom` | Liveness | 8.0 MB | 8.2 MB |
| `yoti-sdk-facecapture` | Face capture | 15.0 MB | 15.9 MB |
| `yoti-sdk-facecapture-unbundled` | Face capture (AI model downloaded via Google Play services) | 8.7 MB | 9.0 MB |

### Module combinations

Modules share common code and dependencies, which are only included once. This means a combination of modules adds less than the sum of its individual modules.

| Combination | armeabi-v7a | arm64-v8a |
|-------------|------------:|----------:|
| ID documents + Supplementary documents | 23.3 MB | 25.5 MB |
| ID documents + Liveness | 29.2 MB | 31.6 MB |
| ID documents + Face capture | 35.5 MB | 38.5 MB |
| ID documents + Supplementary documents + Liveness | 29.5 MB | 31.9 MB |
| ID documents + Supplementary documents + Face capture | 35.8 MB | 38.8 MB |
| ID documents (slim) + Supplementary documents + Liveness | 9.9 MB | 10.2 MB |
| ID documents (slim) + Supplementary documents + Face capture (unbundled) | 10.0 MB | 10.3 MB |

**Notes:**
- The figures above represent estimated Google Play download sizes for an English-language device.
- The space used on the device after installation will probably be larger than the download size, because downloads are compressed.
- If your app already uses some of the SDK's dependencies, the actual increase in size will be smaller, which will benefit you.
- If you distribute a universal APK instead of an App Bundle, it contains native code for every architecture. The SDK's size impact will be considerably larger than shown above.

## Reducing the size of your APK
We recommend that you distribute your app using [App Bundle](https://developer.android.com/guide/app-bundle). This new Google Play feature allows you to use [Play Feature delivery](https://developer.android.com/guide/app-bundle/play-feature-delivery#customize_delivery) which uses advanced capabilities of app bundles, allowing certain features of your app to be delivered conditionally or downloaded on demand.
App Bundle also defer apk generation to Google Play, allowing it to generate minimal APK for each specific device that downloads your app, including only required processor architecture support. 

Also, don't forget to [shrink, obfuscate and optimize](https://developer.android.com/studio/build/shrink-code) your app.  

## Troubleshooting
If you are using the `yoti-sdk-liveness-zoom` module together with the [App Bundle](https://developer.android.com/guide/app-bundle) publishing format, you might encounter the following runtime exception when attempting to open the scan screen:
```
Native library failed to load: null
```
This is caused by a third party library we use. To fix it, you can try adding the following to your `build.gradle` file:
```
android {
    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}
```

### Google Play automatic protection

Google Play's [automatic protection](https://support.google.com/googleplay/android-developer/answer/10183279) (managed from the **Protected with Play** page in Play Console, and sometimes referred to as automatic integrity protection) modifies your app's code after you upload your App Bundle to add Google's own anti-tamper checks. As Google states in its recommended practices, automatic protection *"may not be compatible with other runtime anti-tamper solutions, and trying to use them together may result in user issues"*.

The `yoti-sdk-liveness-zoom` module relies on a third-party library that performs its own runtime integrity checks, so the two protections can conflict. Any app that includes `yoti-sdk-liveness-zoom` and has automatic protection enabled may be affected, regardless of the Yoti SDK version: the app might crash when the user starts the face scan, even if the rest of the flow works as expected.

Because Google applies the protection after upload, such issues would not show up in builds you distribute yourself, such as debug builds, builds uploaded through internal app sharing (which Google does not protect), or builds distributed outside Google Play (for example, Firebase App Distribution).

If automatic protection is enabled for your app, we strongly recommend that you:
- Release every protected build to a Google Play [testing track](https://support.google.com/googleplay/android-developer/answer/9845334) (internal, closed or open testing) before promoting it to production. Google applies protection on all tracks, so a build installed from a testing track matches what your users will receive.
- Install the app from Google Play and complete a full liveness session on a range of devices.

If you need to keep automatic protection enabled and you encounter issues with the face scan, consider using static liveness (the `yoti-sdk-facecapture` or `yoti-sdk-facecapture-unbundled` module) instead of `yoti-sdk-liveness-zoom`. This also requires your backend to request static liveness when creating the session.

## Support
For any questions or support please contact us here: https://support.yoti.com
Once we have answered your question we may contact you again to discuss Yoti products and services. If you'd prefer us not to do this, please let us know when you e-mail.

## License
Yoti Doc Scan Android SDK is under a Proprietary License see this [link](https://www.yoti.com/wp-content/uploads/2019/08/Yoti-Doc-Scan-SDK-Terms.pdf) for more information

We also list all open-source licensed software that has been incorporated into the SDK on files inside `.aar`.
- res/raw/yds_identity_verification_licenses_core.json
- res/raw/yds_identity_verification_licenses_id_document.json
- res/raw/yds_identity_verification_licenses_supplementary_document.json
