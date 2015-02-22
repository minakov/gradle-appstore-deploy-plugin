# gradle-appstore-deploy-plugin

[![Build Status](https://travis-ci.org/minakov/gradle-appstore-deploy-plugin.svg)](https://travis-ci.org/minakov/gradle-appstore-deploy-plugin)
[![Download](https://api.bintray.com/packages/appsense/gradle-plugins/gradle-appstore-deploy-plugin/images/download.svg) ](https://bintray.com/appsense/gradle-plugins/gradle-appstore-deploy-plugin/_latestVersion)

Gradle Plugin for upload Android applications and application details to the [Google Play Store](https://play.google.com/store)

## Applying plugin

### Gradle 2.1+

In whichever `build.gradle` file.

```gradle
plugins {
  id 'info.appsense.appstore' version '0.0.1'
}
```

Note: exact version number must be specified, `+` cannot be used as wildcard.

### All versions of Gradle

1. Add dependency to the __top-level__ `build.gradle` file.

```gradle
 buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'info.appsense:gradle-appstore-deploy-plugin:0.0.1'
    }
}
```

Note: `mavenCentral()` and/or `jcenter()` repository can be specified, `+` can be used as wildcard in version number.

2. Apply plugin and add configuration to `build.gradle` of the application, eg:

```gradle
apply plugin: 'info.appsense.appstore'
```

## Usage

Running the `generateGooglePlayResources<Variant Name>` task will fetch all existing data from the Google Play Store
and generate the required files and folders.

```
[project root]/
    [sourceDir]/
        [variantName]/
            [language]/
                fullDescription.txt
                shortDescription.txt
                video.txt
                title.txt
                recentChangesAlpha.txt
                recentChangesBeta.txt
                recentChangesProduction.txt
                images/
                    icon.png
                    featureGraphic.png
                    promoGraphic.png
                    tvBanner.png
                    screenshots/
                        phone/
                            1.png
                            ..
                            8.png
                        sevenInch/
                            1.png
                            ..
                            8.png
                        tenInch/
                            1.png
                            ..
                            8.png
                        tv/
                            1.png
                            ..
                            8.png
```
*Note: Currently the API not supports downloading the play store graphics. Task created empty files for the image.*

Running the `publishGooglePlayAlphaApplication<Variant Name>`, `publishGooglePlayBetaApplication<Variant Name>`
or `publishGooglePlayProductionApplication<Variant Name>` task will upload an APKs to alpha, beta and production tracks.

Running the `publishGooglePlayResources<Variant Name>` task will upload the descriptions, recent changes and images for
the Google Play Store listing.

*Note: It is not recommended to upload builds to __any track__ more than once a day. The production track should be updated
even less frequently, and with extreme care. See [Google Play Developer API Usage Instructions](https://developers.google.com/android-publisher/api_usage) for more tips.*

*Note: debuggable build types are skipped by default.*

## Credentials

To use the publisher plugin you have to [create a service account](https://developers.google.com/android-publisher/getting_started#setting_up_api_access_clients) for your existing Google Play Account.

Grant at least the following permissions to that service account:

* Edit store listing, pricing & distribution
* Manage Production APKs
* Manage Alpha & Beta APKs
* Manage Alpha & Beta users

Once you finished the setup you have a so called *service account email address* and a *p12 key file* that we will use later on.

If you need to change the password on a p12 certificate the following simple steps will do it.

```bash
openssl pkcs12 -in key.p12 -out /tmp/cert.pem
openssl pkcs12 -export -in /tmp/cert.pem -out /tmp/new.p12
rm -f key.p12 /tmp/cert.pem
```

*Note: this is not the best security practice unless you can be absolutely 100% positive that there are no other
copies of the cert with the old password.*

## Basic configuration

Add to your build.gradle

```gradle
appStoreDeploy {
    googlePlay {
        serviceAccount {
            email = 'your-service-account-email'
            storeFile = file('key.p12')
        }
    }
}
```

## Advanced configuration

Add to your build.gradle

```gradle
appStoreDeploy {
    googlePlay {
        serviceAccount {
            email = 'your-service-account-email'
            storeFile = file('key.p12')
            storePassword = 'notasecret'
            keyAlias = 'privatekey'
            keyPassword = 'notasecret'
        }
    }
    resources {
        sourceDir = 'store-resources'
    }
}
```

## Dependencies

Needs the ```com.android.application``` plugin applied. Supports the Android Application Plugin as of version ```1.0.0```.

## License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
