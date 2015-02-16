package info.appsense.appstore.gradle.plugins.internal;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Collections;

public class AndroidPublisherBuilder {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "AppSense/AppStore-Gradle-Plugins/1.0";
    private static HttpTransport httpTransport;
    private String email;
    private File storeFile;
    private String storePassword;
    private String alias;
    private String keyPassword;

    /**
     * Performs all necessary setup steps for running requests against the API.
     */
    public AndroidPublisher build() throws IOException, GeneralSecurityException {
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

    public AndroidPublisherBuilder setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
        return this;
    }

    public AndroidPublisherBuilder setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public AndroidPublisherBuilder setStorePassword(String storePassword) {
        this.storePassword = storePassword;
        return this;
    }

    public AndroidPublisherBuilder setStoreFile(File storeFile) {
        this.storeFile = storeFile;
        return this;
    }

    public AndroidPublisherBuilder setEmail(String email) {
        this.email = email;
        return this;
    }
}
