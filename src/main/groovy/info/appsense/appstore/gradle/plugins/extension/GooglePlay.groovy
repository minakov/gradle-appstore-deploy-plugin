package info.appsense.appstore.gradle.plugins.extension

import info.appsense.appstore.gradle.plugins.extension.google.ReleaseStrategy
import info.appsense.appstore.gradle.plugins.extension.google.ServiceAccount
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

/**
 * Extension for plugin config properties
 */
class GooglePlay {
    private final Project project
    final ReleaseStrategy releaseStrategy
    final ServiceAccount serviceAccount

    public GooglePlay(Project project, Instantiator instantiator) {
        this.project = project
        releaseStrategy = instantiator.newInstance(ReleaseStrategy, project)
        serviceAccount = instantiator.newInstance(ServiceAccount, project)
    }

    void releaseStrategy(Action<ReleaseStrategy> action) {
        action.execute(releaseStrategy)
    }

    void serviceAccount(Action<ServiceAccount> action) {
        action.execute(serviceAccount)
    }

    public boolean isConfigured() {
        return releaseStrategy.isConfigured() && serviceAccount.isConfigured()
    }
}
