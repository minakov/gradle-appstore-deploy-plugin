package info.appsense.appstore.gradle.plugins.internal

import com.google.api.services.androidpublisher.AndroidPublisher
import info.appsense.appstore.gradle.plugins.extension.google.ServiceAccount

class AndroidPublisherFactory {

    public static AndroidPublisher create(final ServiceAccount serviceAccount) {
        return new AndroidPublisherBuilder()
                .setStoreFile(serviceAccount.getStoreFile())
                .setStorePassword(serviceAccount.storePassword)
                .setAlias(serviceAccount.keyAlias)
                .setKeyPassword(serviceAccount.keyPassword)
                .setEmail(serviceAccount.email)
                .build()
    }
}
