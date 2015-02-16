package info.appsense.appstore.gradle.plugins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import info.appsense.appstore.gradle.plugins.extension.PluginExtension
import info.appsense.appstore.gradle.plugins.tasks.BootstrapResourcesTask
import info.appsense.appstore.gradle.plugins.tasks.PublishApplicationTask
import info.appsense.appstore.gradle.plugins.tasks.PublishResourcesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

/**
 * Created by vladimir.minakov on 16.02.15.
 */
class AppStoreDeployPlugin implements Plugin<Project> {
    private final static String GROUP_NAME = 'AppStore'
    Instantiator instantiator

    @Inject
    public AppStoreDeployPlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    void apply(Project project) {
        if (!project.plugins.hasPlugin(AppPlugin)) {
            throw new IllegalStateException("The 'android' plugin is required.")
        }
        project.extensions.create(PluginExtension.PROPERTY_NAME, PluginExtension, project, instantiator)

        def android = project.property('android') as AppExtension
        android.applicationVariants.all { ApplicationVariant variant ->
            if (variant.buildType.isDebuggable()) {
                return
            }
            project.tasks.create("generateGooglePlayResources${variant.name.capitalize()}", BootstrapResourcesTask, { BootstrapResourcesTask task ->
                task.group = GROUP_NAME
                task.description = "Generate the resources directory structure from the Google Play Store for the ${variant.name} build"
                task.applicationVariant = variant
                task.outputs.upToDateWhen { false }
            })
            project.tasks.create("publishGooglePlayResources${variant.name.capitalize()}", PublishResourcesTask, { PublishResourcesTask task ->
                task.group = GROUP_NAME
                task.description = "Publish application details to the Google Play Store linsting for the ${variant.name} build"
                task.applicationVariant = variant
                task.outputs.upToDateWhen { false }
            })
            ["alpha", "beta", "production"].each {
                project.tasks.create("publishGooglePlay${it.capitalize()}Application${variant.name.capitalize()}", PublishApplicationTask, { PublishApplicationTask task ->
                    task.group = GROUP_NAME
                    task.description = "Upload application to the Google Play Store linsting for the ${variant.name} build"
                    task.applicationVariant = variant
                    task.releaseType = it
                    task.outputs.upToDateWhen { false }
                    task.dependsOn variant.assemble
                })
            }
        }
    }

}
