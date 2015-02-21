package info.appsense.appstore.gradle.plugins.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

public class ProjectFactory {
    private static final String FIXTURES = "src/test/fixtures/android_app"

    public static Project build() {
        Project project = ProjectBuilder.builder().withProjectDir(new File(FIXTURES)).build();
        project.apply plugin: 'com.android.application'
        project.apply plugin: 'info.appsense.appstore'
        project.android {
            compileSdkVersion 21
            buildToolsVersion '21.1.0'

            defaultConfig {
                versionCode 1
                versionName "1.0"
                minSdkVersion 21
                targetSdkVersion 21
            }

            buildTypes {
                release {
                    signingConfig signingConfigs.debug
                }
            }
        }

        return project
    }
}
