package info.appsense.appstore.gradle.plugins

import com.google.api.services.androidpublisher.AndroidPublisher
import info.appsense.appstore.gradle.plugins.internal.AndroidPublisherBuilder
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

/**
 * Extension for plugin config properties
 */
class AppStoreDeployExtension {
    public static final String NAME = "appStoreDeploy"
    ResourcesConfig resources = new ResourcesConfig()
    GooglePlayConfig googlePlay = new GooglePlayConfig()

    static AppStoreDeployExtension from(Project project) {
        return project.property(NAME) as AppStoreDeployExtension
    }

    def resources(Closure closure) {
        ConfigureUtil.configure(closure, resources)
    }

    def googlePlay(Closure closure) {
        ConfigureUtil.configure(closure, googlePlay)
    }

    void isConfigured() {
        resources.isConfigured()
        googlePlay.isConfigured()
    }

    class ResourcesConfig {
        def String sourceDir = "store-resources";

        public void isConfigured() {
            if (sourceDir == null || sourceDir.isEmpty()) {
                throw new IllegalArgumentException("SourceDir is not a invalid.");
            }
        }
    }

    class GooglePlayConfig {
        ReleaseStrategyConfig releaseStrategy = new ReleaseStrategyConfig()
        ServiceAccountConfig serviceAccount = new ServiceAccountConfig()

        def releaseStrategy(Closure closure) {
            ConfigureUtil.configure(closure, releaseStrategy)
        }

        def serviceAccount(Closure closure) {
            ConfigureUtil.configure(closure, serviceAccount)
        }

        public void isConfigured() {
            releaseStrategy.isConfigured()
            serviceAccount.isConfigured()
        }
    }

    class ReleaseStrategyConfig {
        /** Allowed percentage values when doing a staged rollout to production. */
        private final static def PERCENTAGES = [0.5, 1, 5, 10, 20, 50, 100] as double[];
        def int percentageOfUsers = 100;

        public void isConfigured() {
            if (!PERCENTAGES.contains(percentageOfUsers)) {
                throw new IllegalArgumentException("PercentageOfUsers [" + percentageOfUsers + "] is not a invalid. Expects: [0.5, 1, 5, 10, 20, 50, 100]");
            }
        }
    }

    class ServiceAccountConfig {
        def String clientEmail;
        def File keyStoreFile;
        def KeyStoreConfig keyStore = new KeyStoreConfig()
        def String privateKeyId;
        def String privateKeyPem;

        public AndroidPublisher buildPublisher() {
            def AndroidPublisherBuilder builder = new AndroidPublisherBuilder()
                    .setClientEmail(clientEmail)
                    .setStorePassword(keyStore.storePassword)
                    .setAlias(keyStore.keyAlias)
                    .setKeyPassword(keyStore.keyPassword)

            if (keyStoreFile != null) {
                builder.setStoreFile(keyStoreFile)
            } else if (privateKeyPem != null) {
                builder.setPrivateKeyPem(privateKeyPem).setPrivateKeyId(privateKeyId)
            } else {
                builder.setStoreFile(keyStore.file)
            }
            return builder.build()
        }

        def keyStore(Closure closure) {
            ConfigureUtil.configure(closure, keyStore)
        }

        public void isConfigured() {
            if (clientEmail == null || clientEmail.isEmpty()) {
                throw new IllegalArgumentException("Service account client email is required.");
            }
            if (keyStoreFile != null) {
                if (!keyStoreFile.exists()) {
                    throw new IllegalArgumentException("Service account P12 key file [" + keyStoreFile + "] not found.");
                }
            } else if (privateKeyPem != null) {
                if (privateKeyPem.isEmpty()) {
                    throw new IllegalArgumentException("Service account private key is empty.");
                }
                if (privateKeyId == null || privateKeyId.isEmpty()) {
                    throw new IllegalArgumentException("Service account private key id is required.");
                }
            } else {
                keyStore.isConfigured();
            }
        }
    }

    class KeyStoreConfig {
        def File file;
        def String storePassword = 'notasecret';
        def String keyAlias = 'privatekey';
        def String keyPassword = 'notasecret';

        public void isConfigured() {
            if (file == null || !file.exists()) {
                throw new IllegalArgumentException("Service account P12 key file is required. Filename [" + file + "] not found.");
            }
        }
    }
}
