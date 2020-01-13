# Changelog
All notable changes to this project will be documented in this file.

## [2.0.0] - 2020-01-10
### Added
- Big changes in this release where our SDK has now been modularized:
Each feature has its independent dependency therefore, integrators only need to include the dependencies of the features they are interested in.
This means that current integrators will need to change their dependencies as yoti-sdk-doc-scan doesn't exist anymore, and include one for Document Capture (yoti-sdk-doc-scan) and/or one for Zoom Liveness (yoti-sdk-liveness-zoom).
- We have also added some new errors for integrators trying to use features whose dependencies haven't been added to their Gradle files.

### Changed
- We have fixed an issue with the colours overriding process and changed the names of our colours.
- Fixed an issue in the remaining Zoom Liveness attempts count.
- Fixed issue where the 2002 error code (Session expired) was not being returned properly to the host app.
- Fixed a crash in Zoom Liveness when phone was low in resources and activity had to be recreated.
- Fixed issue with the height of the country selection button.

## [1.1.3] - 2019-12-03
### Added
- Hotfix to update our legal requirements information.

## [1.1.2] - 2019-11-18
### Added
- Hotfix to solve some obfuscation issues.

## [1.1.1] - 2019-10-31
### Added
- Hotfix to solve an internal dependency issue as Facetec library is not in Maven anymore. 

## [1.1.0] - 2019-10-29
### Added
- Adding Zoom Liveness functionality:
Integrators will now be able to verify if an user is a real person.

## [1.0.0] - 2019-09-30
### Added
- This is the first release of our Yoti Doc Scan SDK.
- Integrators will be able to easily scan documents in their apps.
