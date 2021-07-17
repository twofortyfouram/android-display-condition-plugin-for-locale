# Deprecation
This example is deprecated, as plug-in conditions can no longer be reliably supported on the latest versions of Android due to restrictions on background processes.

# Overview
[Locale](https://play.google.com/store/apps/details?id=com.twofortyfouram.locale) allows developers to create plug-in conditions and settings.  The android-display-condition-plugin-for-locale implements an example plug-in condition.  This project is the final layer of the [Locale Developer Platform documentation](http://www.twofortyfouram.com/developer).

Although there are multiple ways to approach building a plug-in condition, we recommend forking this project as the starting point.


# Compatibility
The application is compatible and optimized for Android API Level 14 and above.


## Forking this Project
The following steps are necessary to fork this project

1. Rename the package name in AndroidManifest.xml
1. Rename the package name in proguard-project.txt
1. Optional: For CircleCI continous integration (CI)
    1. Create a [Firebase](https://firebase.google.com) project
    1. Configure the Firebase project with a [service account](https://firebase.google.com/docs/test-lab/continuous)
    1. On CircleCI, add environment variables for `GCLOUD_SERVICE_KEY_BASE_64` which is the base64 encoded JSON service key,  `GCLOUD_PROJECT_ID` which is the project ID, and `GCLOUD_DEFAULT_BUCKET` which is the default free bucket included with Firebase Test Lab.
