package info.appsense.appstore.gradle.plugins.extension

import info.appsense.appstore.gradle.plugins.extension.google.ReleaseStrategy
import info.appsense.appstore.gradle.plugins.extension.google.ServiceAccount
import org.gradle.api.Action
import org.gradle.internal.reflect.Instantiator

/**
 * Extension for plugin config properties
 */
class GooglePlay {
    final ReleaseStrategy releaseStrategy
    final ServiceAccount serviceAccount

    public GooglePlay(Instantiator instantiator) {
        releaseStrategy = instantiator.newInstance(ReleaseStrategy)
        serviceAccount = instantiator.newInstance(ServiceAccount)
    }

    void releaseStrategy(Action<ReleaseStrategy> action) {
        action.execute(releaseStrategy)
    }

    void serviceAccount(Action<ServiceAccount> action) {
        action.execute(serviceAccount)
    }

    public void isConfigured() {
        releaseStrategy.isConfigured()
        serviceAccount.isConfigured()
    }
}
