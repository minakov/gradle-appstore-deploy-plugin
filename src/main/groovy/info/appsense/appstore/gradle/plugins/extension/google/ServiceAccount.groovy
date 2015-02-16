package info.appsense.appstore.gradle.plugins.extension.google

import org.gradle.api.Project

public class ServiceAccount {
    private final Project project
    def String email;
    def storeFile;
    def String storePassword = 'notasecret';
    def String keyAlias = 'privatekey';
    def String keyPassword = 'notasecret';

    ServiceAccount(Project project) {
        this.project = project
    }

    File getStoreFile() {
        return project.file(storeFile)
    }

    public boolean isConfigured() {
        if (email == null || email.isEmpty()) {
            project.logger.warn("Service account email is required.");
            return false;
        }
        if (storeFile == null || !project.file(storeFile).exists()) {
            project.logger.warn("Service account P12 key file is required. Filename [" + project.file(storeFile) + "] not found.");
            return false;
        }
        return true;
    }
}
