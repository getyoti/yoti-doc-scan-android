![YotiBanner](./yoti_banner.png)

## Yoti Doc Scan - Android SDK
Yoti is an identity checking platform that allows organisations to verify who people are, online and in person.
The Yoti SDK, allows the user to take a photo of their ID, we then verify this instantly and prepare a response, which your system can then retrieve on your hosted site.
Further information can be found [here](https://developers.yoti.com/yoti-doc-scan)

## Table of Contents
- [1. Requirements](#requirements)
- [2. Set up the SDK](#setup-the-sdk)
    - [2.1. Proguard](#proguard)
- [3. Start the SDK](#start-the-sdk)
- [4. Retrieve status of the session](#retrieve-status-of-the-session)
    - [4.1. Possible status for the session](#possible-status-for-the-session)
- [5. Customization](#customisation)
    - [5.1. Font Colour](#font-colour)
    - [5.2. Colours](#colours)
- [6. Support](#support)
- [7. License](#license)

## Requirements
You have setup the Yoti Doc Scan SDK on your backend, you can find the documentation [here](https://developers.yoti.com/yoti-doc-scan/getting-started)

Minimum Android version supported: 21

Currently targeting Android version: 28

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
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-doc-scan:2.0.0'
    implementation 'com.yoti.mobile.android.sdk:yoti-sdk-liveness-zoom:2.0.0'
}
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

### Proguard
If you are using Proguard, you will need to add the following lines in its configuration file:

```
-keep class com.yoti.** { *; }
-keep class com.microblink.** { *; }
-keep class com.microblink.**$* { *; }
-dontwarn com.microblink.**
-keep class com.facetec.zoom.** { *; }
-dontwarn javax.annotation.Nullable
```

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
                    .setSessionToken("<Your Session Token>")
                    .start(this)
        }

    }
}
```

## Retrieve status of the session

Once the user completed the flow in the SDK, the user will be redirected to the Activity which started the SDK.
You can then retrieve the current status of the current session like that:

```kotlin
class MainActivity : AppCompatActivity() {

    private val yotiSdk = YotiSdk(this)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == YOTI_DOCS_REQUEST_CODE) {

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
| 5000              | No camera (when user's camera was not found and file upload is not allowed)          | No |
| 5002              | No more local tries for the liveness flow          | Yes |
| 5003              | SDK is out-of-date - please update the SDK to the latest version          | No |
| 5004              | Unexpected internal error          | No |
| 5005              | Unexpected document scanning error          | No |
| 5006              | Unexpected liveness error          | No |
| 6000              | Document Capture dependency not found error          | No |
| 6001              | Liveness Zoom dependency not found error          | No |

## Customisation
You can customise the appearance of the screens of the SDK by overriding some of the colours.

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

## Support
If you have any other questions please do not hesitate to contact sdksupport@yoti.com.
Once we have answered your question we may contact you again to discuss Yoti products and services. If you'd prefer us not to do this, please let us know when you e-mail.

## License
Yoti Doc Scan Android SDK is under a Proprietary License see this [link](https://www.yoti.com/wp-content/uploads/2019/08/Yoti-Doc-Scan-SDK-Terms.pdf) for more information