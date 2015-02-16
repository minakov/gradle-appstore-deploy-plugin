package info.appsense.appstore.gradle.plugins.extension.google

import org.gradle.api.Project

public class ReleaseStrategy {
    /** Allowed percentage values when doing a staged rollout to production. */
    private final static def PERCENTAGES = [0.5, 1, 5, 10, 20, 50, 100] as double[];
    private final Project project
    def int percentageOfUsers = 100;

    ReleaseStrategy(Project project) {
        this.project = project
    }

    public boolean isConfigured() {
        if (!PERCENTAGES.contains(percentageOfUsers)) {
            project.logger.warn("PercentageOfUsers [" + percentageOfUsers + "] is not a invalid. Expects: [0.5, 1, 5, 10, 20, 50, 100]");
            return false;
        }
        return true;
    }
}
