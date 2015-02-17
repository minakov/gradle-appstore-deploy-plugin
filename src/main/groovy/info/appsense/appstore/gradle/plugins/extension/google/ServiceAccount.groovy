package info.appsense.appstore.gradle.plugins.extension.google

import com.google.api.services.androidpublisher.AndroidPublisher
import info.appsense.appstore.gradle.plugins.internal.AndroidPublisherBuilder

public class ServiceAccount {
    def String email;
    def File storeFile;
    def String storePassword = 'notasecret';
    def String keyAlias = 'privatekey';
    def String keyPassword = 'notasecret';

    public AndroidPublisher buildPublisher() {
        return new AndroidPublisherBuilder()
                .setStoreFile(storeFile)
                .setStorePassword(storePassword)
                .setAlias(keyAlias)
                .setKeyPassword(keyPassword)
                .setEmail(email)
                .build()
    }

    public void isConfigured() {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Service account email is required.");
        }
        if (storeFile == null || !storeFile.exists()) {
            throw new IllegalArgumentException("Service account P12 key file is required. Filename [" + storeFile + "] not found.");
        }
    }
}
