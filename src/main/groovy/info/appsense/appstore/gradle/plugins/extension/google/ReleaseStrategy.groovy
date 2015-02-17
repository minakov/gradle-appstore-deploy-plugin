package info.appsense.appstore.gradle.plugins.extension.google

public class ReleaseStrategy {
    /** Allowed percentage values when doing a staged rollout to production. */
    private final static def PERCENTAGES = [0.5, 1, 5, 10, 20, 50, 100] as double[];
    def int percentageOfUsers = 100;

    public void isConfigured() {
        if (!PERCENTAGES.contains(percentageOfUsers)) {
            throw new IllegalArgumentException("PercentageOfUsers [" + percentageOfUsers + "] is not a invalid. Expects: [0.5, 1, 5, 10, 20, 50, 100]");
        }
    }
}
