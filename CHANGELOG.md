# Changelog
All notable changes to this project will be documented in this file.
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
