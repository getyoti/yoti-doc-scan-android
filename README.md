![YotiBanner](./yoti_banner.png)

[![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/getyoti/yoti-doc-scan-android?label=latest%20release)](https://github.com/getyoti/yoti-doc-scan-android/releases)

## Yoti Doc Scan - Android SDK

Yoti is an identity checking platform that allows organisations to verify who people are, online and in person.
The Yoti SDK, allows the user to take a photo of their ID, we then verify this instantly and prepare a response, which your system can then retrieve on your hosted site.
Further information can be found [here](https://developers.yoti.com/yoti-doc-scan)

## Table of Contents
- [Requirements](#requirements)
- [Set up the SDK](#setup-the-sdk)
    - [R8 / Proguard](#r8-and-proguard)
- [Start the SDK](#start-the-sdk)
- [Retrieve status of the session](#retrieve-status-of-the-session)
    - [Possible status for the session](#possible-status-for-the-session)
- [Customization](#customisation)
    - [Font Colour](#font-colour)
    - [Colours](#colours)
- [Support](#support)
- [License](#license)

## Requirements
You have setup the Yoti Doc Scan SDK on your backend, you can find the documentation [here](https://developers.yoti.com/yoti-doc-scan/getting-started)

Minimum Android version supported: 21

Currently targeting Android version: 32

Note: we are using libraries from Android Jetpack. If you are still using the original Android Support Libraries you may encounter some issues when trying to use our SDK.
We strongly recommend you to migrate your app to the new Androidx libraries: https://developer.android.com/jetpack/androidx/migrate

## Setup the SDK

Make sure you have mavenCentral and microblink repository added in your allProjects entry in your root build.gradle:

```groovy
allprojects {
    repositories {
        mavenCentral()
        maven { url 'https://maven.microblink.com' }
        maven { url "https://jitpack.io" }
        ...
    }
    ...
}
```

The Yoti SDK is composed of multiple feature modules. Each feature is optional, but you must include at least one to use the SDK.

The modules you include must match those requested by your backend. Attempts to use a module you haven't included will fail at runtime with a [600x response code](#possible-status-for-the-session).

Add modules you require to your build.gradle:
```groovy
dependencies {
    //If you need document capture
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-doc-scan:3.2.1'

    //If you need supplementary documents
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-doc-scan-sup:3.2.1'

    //If you need liveness
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-liveness-zoom:3.2.1'

    //If you need selfie capture
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-facecapture:3.2.1'
    //Or if you want the version without an embedded AI model, which is ~20 MB smaller in size
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-facecapture-unbundled:3.2.1'
}
```

As you can see above, there are two options to add the `facecapture` module to your app:
- `yoti-sdk-facecapture` embeds an AI model for face detection.
- `yoti-sdk-facecapture-unbundled` will manage the download of the AI model via Google Play Services the first time you start using the AI model and thus is ~20 MB smaller in size. Additionally, you can add the following metadata to your `AndroidManifest.xml` to get the model downloaded as soon as the app is installed:
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

### R8 and Proguard

If you are using R8 the shrinking and obfuscation rules are included automatically.

ProGuard users must manually add the options from [proguard-rules.pro](https://github.com/getyoti/yoti-doc-scan-android/blob/master/app/proguard-rules.pro). 
You might also need rules for [retrofit](https://github.com/square/retrofit/blob/5c6620/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro), [OkHttp](https://github.com/square/okhttp/blob/a16ec15/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro), [Okio](https://github.com/square/okio/blob/f906821e6/okio/src/jvmMain/resources/META-INF/proguard/okio.pro) and [Gson](https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg) which are dependencies of this library.

## Start the SDK

After creating the session on your backend you will get a session ID and a session token, you will need to provide them to your app.
Then you can start the SDK like this:

```kotlin
class MainActivity : AppCompatActivity() {

    private val yotiSdk = YotiSdk(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        startButton.setOnClickListener {
            yotiSdk
                    .setSessionId("<Your Session ID>")
                    .setClientSessionToken("<Your Session Token>")
                    .start(this)
        }

    }
}
```

By default Activity request code is 9001 and you can handle it in `onActivityResult` by checking `YOTI_SDK_REQUEST_CODE`, but if you prefer to customise it, it is possible by specifying it in the `start` method:
```kotlin
            yotiSdk
                    .setSessionId("<Your Session ID>")
                    .setClientSessionToken("<Your Session Token>")
                    .start(this, <Your Request Code>)
```

## Retrieve status of the session

Once the user completed the flow in the SDK, the user will be redirected to the Activity which started the SDK.
You can then retrieve the current status of the current session like that:

```kotlin
class MainActivity : AppCompatActivity() {

    private val yotiSdk = YotiSdk(this)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == YOTI_SDK_REQUEST_CODE) {

            val sessionStatusCode = yotiSdk.sessionStatusCode
            val sessionStatusDescription = yotiSdk.sessionStatusDescription

            result_text.text = "Result code: $sessionStatusCode Message: $sessionStatusDescription"
        }
    }
}
```


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
## Reducing the size of your APK
We recommend that you distribute your app using [App Bundle](https://developer.android.com/guide/app-bundle). This new Google Play feature allows you to use [Play Feature delivery](https://developer.android.com/guide/app-bundle/play-feature-delivery#customize_delivery) which uses advanced capabilities of app bundles, allowing certain features of your app to be delivered conditionally or downloaded on demand.
App Bundle also defer apk generation to Google Play, allowing it to generate minimal APK for each specific device that downloads your app, including only required processor architecture support. 

Also, don't forget to [shrink, obfuscate and optimize](https://developer.android.com/studio/build/shrink-code) your app.  

## Troubleshooting
If you are using the `yoti-sdk-liveness-zoom` module together with the [App Bundle](https://developer.android.com/guide/app-bundle) publishing format, you might encounter the following runtime exception when attempting to open the scan screen:
```
Native library failed to load: null
```
This is caused by a third party library we use. Disabling uncompressed native libraries inside your project's `gradle.properties` file solves the issue:
```
android.bundle.enableUncompressedNativeLibs = false
```

## Support
If you have any other questions please do not hesitate to contact clientsupport@yoti.com.
Once we have answered your question we may contact you again to discuss Yoti products and services. If you'd prefer us not to do this, please let us know when you e-mail.

## License
Yoti Doc Scan Android SDK is under a Proprietary License see this [link](https://www.yoti.com/wp-content/uploads/2019/08/Yoti-Doc-Scan-SDK-Terms.pdf) for more information

We also list all open-source licensed software that has been incorporated into the SDK on files inside `.aar`.
- res/raw/yds_identity_verification_licenses_core.json
- res/raw/yds_identity_verification_licenses_id_document.json
- res/raw/yds_identity_verification_licenses_supplementary_document.json
