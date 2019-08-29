## Yoti Doc Scan - Android SDK
Yoti is an identity checking platform that allows organisations to verify who people are, online and in person.
The Yoti Doc Scan SDK, allows the user to take a photo of their ID, we then verify this instantly and prepare a response, which your system can then retrieve on your hosted site.
Further information can be found [here](https://developers.yoti.com/yoti-doc-scan)

## Requirements
You have setup the Yoti Doc Scan SDK on your backend, you can find the documentation [here](https://developers.yoti.com/yoti-doc-scan/getting-started)

Minimum Android version supported: 21

Currently targeting Android version: 28

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

Add the dependency to your build.gradle

```groovy
dependencies {
    implementation 'com.yoti.mobile.android.sdk:yoti-doc-scan:1.0.0'
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

## Start the SDK

After creating the session on your backend you will get a session ID and a session token, you will need to provide them to your app.
Then you can start the SDK like this:

```kotlin
class MainActivity : AppCompatActivity() {

    private val yotiDocScan = YotiDocScan(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        start_button.setOnClickListener {
            yotiDocScan
                    .setSessionId("<Your Session ID>")
                    .setSessionToken("<Your Session Token>")
                    .start(this)
        }

    }
}
```

## Retrieve status of the session

Once the user completed the flow in the SDK, the user will be redirected to the Activity which started the SDK.
You can then retreive the current status of the current session like that:

```kotlin
class MainActivity : AppCompatActivity() {

    private val yotiDocScan = YotiDocScan(this)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == YOTI_DOCS_REQUEST_CODE) {

            val sessionStatusCode = yotiDocScan.sessionStatusCode
            val sessionStatusDescription = yotiDocScan.sessionStatusDescription

            result_text.text = "Result code: $sessionStatusCode Message: $sessionStatusDescription"
        }
    }
}
```


### Possible status for the session

| Code              | Message                      | Retry possible for the same session                    |
| ----------------- | ---------------------------- | ---------------------------------- |
| 0                 | Result with success          | No                                 |
| 1000              | No error occurred - the end-user cancelled the session for an unknown reason.          | Yes |
| 2000              | Unauthorised request (wrong or expired session token).          | Yes |
| 2001              | Session not found.          | Yes |
| 2003              | SDK launched without session Token.          | Yes |
| 2004              | SDK launched without session ID.          | Yes |
| 3000              | Yoti's services are down or unable to process the request.          | Yes |
| 3001              | An error occurred during a network request          | Yes |
| 3002              | User has no network          | Yes |
| 4000              | The user did not grant permissions to the camera          | Yes |
| 5000              | No camera.(When user's camera was not found and file upload is not allowed)          | No |
| 6000              | SDK is out-of-date - please update the SDK to the latest version          | No |
| 6001              | Unexpected internal error          | No |
| 6002              | Unexpected document scanning error          | No |


## Customisation
You can customise the appearance of the screens of the SDK by overriding some styles.

### Font Family and Font Colour
In order to change the font family and the font colour you just need to define the following styles:

```xml
<style name="Yoti.v3.Font.DisplayBold">
<style name="Yoti.v3.Font.DisplayRegular">
<style name="Yoti.v3.Font.TextBold">
<style name="Yoti.v3.Font.TextRegular">
<style name="Yoti.v3.Font.TextRegular.RadioButton">
<style name="Yoti.v3.Font.TextBold.RadioButton">
```

If you just want to change the colour of the font, then override the following colour:

```xml
<color name="yotiDocs_colorFont">
```

### Primary Colours
In order to change the colours of the different elements of the screens you just need to declare the following colours:

- For the main app branding colour:
```xml
<color name="yotiDocs_colorPrimary">
```

- For the darker variant:
```xml
<color name="yotiDocs_colorPrimaryDark">
```

- For the UI contols like checkboxes and text fields:
```xml
<color name="yotiDocs_colorAccent">
```

## License
Yoti Doc Scan Android SDK is under a Proprietary License see this [link](https://www.yoti.com/wp-content/uploads/2019/08/Yoti-Doc-Scan-SDK-Terms.pdf) for more information