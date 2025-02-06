# Changelog
All notable changes to this project will be documented in this file.
## [4.0.0] - 2025-03-11
### Changed
- Document processing improvements. This introduces a small breaking change requiring replacing the
  "microblink" maven url with the "regulaforensics" one in your allProjects entry in your root build.gradle
  file (see README - Setup the SDK). You can also remove any "microblink"-related proguard rules you have.
- Addressed compatibility issues when building with Android Gradle Plugin 8+
- Various bug fixes and improvements

## [3.5.2] - 2024-09-12
- Updated internal dependency version

## [3.5.1] - 2024-10-18
- New document supported: USA Passport card
- Updated internal dependency version

## [3.5.0] - 2024-09-17
## Changed
- Updated illustrations
- Updated privacy link
- Updated NFC screens
- Improvements to the in-session feedback screens
- Updated copies
- Bug fixes and code improvements

## [3.4.0] - 2024-06-06
## Added
- Added a new slim variant of the ID document capture module, which includes a reduced feature set:
  no automatic capture via OCR and no NFC capture. Using this variant results in a significantly
  smaller APK size (by about 15Mb).

## Changed
- Updated to Kotlin version 1.8

## [3.3.0] - 2024-04-24
## Added
- New error dialog and session status (3003) for slow internet timeouts

### Changed
- Updated the document selection, capture, guidelines, and review screens
- Updated the biometric consent screen
- Updated the label for Israeli National ID
- Bug fixes and code improvements

## [3.2.2] - 2024-01-31
### Changed
- Updated licence keys.
- Updated proguard rules.
- Bug fixes and code improvements.

## [3.2.1] - 2023-11-14
### Changed
- Updated internal dependency version.

## [3.2.0] - 2023-10-19
### Added
- Implemented font type customisation.
- Added search functionality to the issuing country search.
- Allow retention logic in the biometric consent.

### Changed
- Improved the supplementary document guidelines screen.
- Updated the biometric consent screen copy.
- Updated the copy on Italian national ID.
- Bug fixes and code improvements

## [3.1.1] - 2023-07-12
### Changed
- General bug fixes.

## [3.1.0] - 2023-05-18
### Added
- New document types supported: CitizenCard, Post Office PASS Card, SCIS and Canadian Health card

### Changed
- Updated the copy on the dead end screens.
- Changed the label for Canadian residence permit.
- General bug fixes and code improvements.

## [3.0.1] - 2023-04-03
### Changed
Hotfix: Removed unused resources

## [3.0.0] - 2023-02-27
### Added
- New language support for Arabic, Dutch, French, German, Italian, Russian, Spanish and Turkish
- New session statuses for UNSUPPORTED_CONFIGURATION and MANDATORY_DOCUMENT_NOT_PROVIDED
- New alternative version of the facecapture module without an embedded AI model, which is ~20 MB smaller in size

### Changed
- General improvements and bug fixes
- Updated Kotlin version to 1.6.21
- Updated proguard rules

### Migrating from 2.x.x
- Update the version of the Yoti SDK dependencies in your build.gradle file
- Update your project's Kotlin version to 1.6 or newer
- OPTIONAL: If you use yoti-sdk-facecapture, consider the new alternative yoti-sdk-facecapture-unbundled, without an embedded AI model, ~20 MB smaller in size (see README for details)
- OPTIONAL: If you want to avoid situations where our SDK would be shown in a different language than the one your app is using, you need to declare the languages your app supports (see README for details)
- OPTIONAL: Handle the new session status codes outlined above and in the README

## [2.9.3] - 2023-01-05
### Changed
- Hotfix: document scan navigation flow

## [2.9.2] - 2022-12-22
### Changed
- Hotfix: document scan navigation flow

## [2.9.1] - 2022-11-04
### Changed
- Hotfix: document scan ocr extraction

## [2.9.0] - 2022-08-24
### Added
- Handle retry when liveness check fails due to user error
- Update biometric consent copy
- Improve in-session feedback handling
- Add "Front/Back" tabs for 2-sided documents on in-session feedback screens
- In-session feedback for expired documents
- Provide user guidance when we can't process pdf uploaded as a supplementary document
- Handle USA Permanent Residence card
- Introduce a new error screen for supplementary document pdf upload
- Bug fixes

## [2.8.1] - 2022-03-07
### Fixed
- Manifest merge issue solved.

## [2.8.0] - 2022-03-02
## Critical issue detected on this version (Manifest merge issue on client side), please use 2.8.1
### Added
- Support for Filipino Phil Sys ID & UMID.
- Support for Young Scot Card.  
- Improvements on document upload feedback: quality error detection. 
- Improvements on NFC scan flow.  

## [2.7.0] - 2021-09-21
### Added
- New verification step: Face capture. With it users can provide a selfie of themselves for face match check (ID document photo vs face capture) 
- In Session Feedback: users will receive immediate feedback about the ID document capture quality and validation.

## [2.6.1] - 2021-03-24
### Fixed
- Solved issue happened in some sessions.

## [2.6.0] - 2021-03-05
### Added
- Support for custom privacy policy link.
- Support third party identity check when it is required.
- Multi page PDF viewer.
- Error management: return error code 5009 when it is not possible to write/read device cache (storage error)
- New documents supported: NEXUS Card and Bank Statement.

## [2.5.1] - 2020-11-19
### Added
- YotiSDK Activity request code customisation.

## [2.5.0] - 2020-11-18
### Added
- Biometric consent request: Integrators will now be able to ask for the biometric consent before liveness check.
- Canada Service setup.

## [2.4.0] - 2020-09-23
### Added
- Supplementary documents functionality: Integrators will now be able to verify the user's address by supplementary documents module integration.
Documents supported: Utility Bill, Council Tax Bill & Phone Bill.

## [2.3.2] - 2020-08-03
### Added
- Support Filipino Professional ID & Voter ID
### Fixed
- Solved NFC brand new passport reading issue
- Solved TimeZone issue when retrieving OCR data

## [2.3.1] - 2020-07-21
### Fixed
- Solved incompatibility issue with **Firebase Performance gradle plugin**

## [2.3.0] - 2020-06-26
### Changed
- Zoom Liveness flow update

## [2.2.0] - 2020-05-01
### Added
- Support for NFC read of compatible ePassports
- New documents supported: Philippines SSS ID (Social Security System ID) and Postal ID, and British Residence Permit

## [2.1.0] - 2020-03-03
### Added
- Support Aadhaar card and PAN card
- Integrators would be able to request more than 1 ID document per session.

### Migrating from previous versions
`YotiSdk#YOTI_DOCS_REQUEST_CODE` has been deprecated, please use `YotiSdk#YOTI_SDK_REQUEST_CODE` instead.

## [2.0.0] - 2020-01-10
### Added
- Big changes in this release where our SDK has now been modularized:
Each feature has its own dependency therefore, integrators only need to include the dependencies of the features they are interested in.
This means that current integrators will need to change their dependencies as yoti-sdk-doc-scan doesn't exist anymore, and include one for Document Capture (yoti-sdk-doc-scan) and/or one for Zoom Liveness (yoti-sdk-liveness-zoom).
- We have also added some new errors for integrators trying to use features whose dependencies haven't been added to their Gradle files.

### Fixed
- We have fixed an issue with the colours overriding process and changed the names of our colours.
- Fixed a problem when counting the remaining Zoom Liveness attempts.
- Fixed issue where the 2002 error code (Session expired) was not being returned properly to the host app.
- Fixed a crash in Zoom Liveness when phone was low in resources and activity had to be recreated.
- Fixed issue with the height of the country selection button.

### Migrating from 1.x
- Update dependencies in your build.gradle:
implementation 'com.yoti.mobile.android.sdk:yoti-doc-scan:1.1.3'
-> 
implementation 'com.yoti.mobile.android.sdk:yoti-sdk-doc-scan:2.0.0'
implementation 'com.yoti.mobile.android.sdk:yoti-sdk-liveness-zoom:2.0.0'
- Replace all references to com.yoti.mobile.android.yotidocs with com.yoti.mobile.android.yotisdkcore
- Replace all references to YotiDocScan with YotiSdk
- OPTIONAL: If you don't need zoom liveness, remove the yoti-sdk-liveness-zoom dependency
- OPTIONAL: If you only need liveness, remove the yoti-sdk-doc-scan dependency
- OPTIONAL: If you followed either of the above steps, add handling for error code 6000 or 6001 from the SDK

## [1.1.3] - 2019-12-03
### Fixed
- Hotfix to update our legal requirements information.

## [1.1.2] - 2019-11-18
### Fixed
- Hotfix to solve some obfuscation issues.

## [1.1.1] - 2019-10-31
### Fixed
- Hotfix to solve an internal dependency issue as Facetec library is not in Maven anymore. 

## [1.1.0] - 2019-10-29
### Added
- Adding Zoom Liveness functionality:
Integrators will now be able to verify if an user is a real person.

## [1.0.0] - 2019-09-30
### Added
- This is the first release of our Yoti Doc Scan SDK.
- Integrators will be able to easily scan documents in their apps.
