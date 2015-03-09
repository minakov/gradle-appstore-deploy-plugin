package info.appsense.appstore.gradle.plugins.internal

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.SecurityUtils
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes

import java.security.GeneralSecurityException
import java.security.PrivateKey

/**
 * Created by vladimir.minakov on 05.03.15.
 */
class AndroidPublisherBuilder {
    static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    static final String APPLICATION_NAME = "AppSense/AppStore-Gradle-Plugins/1.0";
    static HttpTransport httpTransport;
    String email;
    File storeFile;
    String storePassword;
    String alias;
    String keyPassword;

    /**
     * Performs all necessary setup steps for running requests against the API.
     */
    def AndroidPublisher build() throws IOException, GeneralSecurityException {
        if (httpTransport == null) {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        }

        PrivateKey key = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(),
                new FileInputStream(storeFile), storePassword, alias, keyPassword);

        // Build service account credential.
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(email)
                .setServiceAccountScopes(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER))
                .setServiceAccountPrivateKey(key)
                .build();

        // Set up and return API client.
        return new AndroidPublisher.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    def AndroidPublisherBuilder setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
        return this;
    }

    def AndroidPublisherBuilder setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    def AndroidPublisherBuilder setStorePassword(String storePassword) {
        this.storePassword = storePassword;
        return this;
    }

    def AndroidPublisherBuilder setStoreFile(File storeFile) {
        this.storeFile = storeFile;
        return this;
    }

    def AndroidPublisherBuilder setEmail(String email) {
        this.email = email;
        return this;
    }
}
