package info.appsense.appstore.gradle.plugins.extension

import org.gradle.api.Project

class Resources {
    private final Project project
    def sourceDir = "store-resources";

    Resources(Project project) {
        this.project = project
    }

    public boolean isConfigured() {
        if (sourceDir == null || sourceDir.isEmpty()) {
            project.logger.warn("SourceDir is not a invalid.");
            return false;
        }
        return true;
    }
}
