package info.appsense.appstore.gradle.plugins.extension

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

/**
 * Extension for plugin config properties
 */
class PluginExtension {
    public static final String PROPERTY_NAME = "appStoreDeploy"
    private final Project project
    final GooglePlay googlePlay
    final Resources resources

    public static PluginExtension from(Project project) {
        return project.property(PROPERTY_NAME) as PluginExtension
    }

    public PluginExtension(Project project, Instantiator instantiator) {
        this.project = project
        googlePlay = instantiator.newInstance(GooglePlay, project)
        resources = instantiator.newInstance(Resources, project)
    }

    void googlePlay(Action<GooglePlay> action) {
        action.execute(googlePlay)
    }

    void resources(Action<Resources> action) {
        action.execute(resources)
    }

    public boolean isConfigured() {
        return googlePlay.isConfigured() && resources.isConfigured()
    }
}
