package info.appsense.appstore.gradle.plugins.internal

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.PemReader
import com.google.api.client.util.SecurityUtils
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes

import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec

/**
 * Created by vladimir.minakov on 05.03.15.
 */
class AndroidPublisherBuilder {
    static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance()
    static final String APPLICATION_NAME = "AppSense/AppStore-Gradle-Plugins/1.0"
    static HttpTransport httpTransport
    String clientEmail
    File storeFile
    String storePassword
    String alias
    String keyPassword
    String privateKeyPem
    String privateKeyId

    /**
     * Performs all necessary setup steps for running requests against the API.
     */
    def AndroidPublisher build() throws IOException, GeneralSecurityException {
        if (httpTransport == null) {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        }

        // Build service account credential.
        GoogleCredential.Builder builder = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(clientEmail)
                .setServiceAccountScopes(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER))

        if (storeFile != null && storeFile.exists()) {
            builder.setServiceAccountPrivateKey(SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(),
                    new FileInputStream(storeFile), storePassword, alias, keyPassword))
        } else if (privateKeyPem != null && !privateKeyPem.isEmpty()) {
            PemReader pemReader = new PemReader(new StringReader(privateKeyPem))
            PemReader.Section section = pemReader.readNextSection()
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(section.getBase64DecodedBytes())
            builder.setServiceAccountPrivateKey(KeyFactory.getInstance("RSA").generatePrivate(keySpec))
                    .setServiceAccountPrivateKeyId(privateKeyId)
        }

        // Set up and return API client.
        return new AndroidPublisher.Builder(httpTransport, JSON_FACTORY, builder.build())
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

    def AndroidPublisherBuilder setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
        return this;
    }

    def AndroidPublisherBuilder setPrivateKeyPem(String privateKeyPem) {
        this.privateKeyPem = privateKeyPem;
        return this;
    }

    def AndroidPublisherBuilder setPrivateKeyId(String privateKeyId) {
        this.privateKeyId = privateKeyId;
        return this;
    }
}
