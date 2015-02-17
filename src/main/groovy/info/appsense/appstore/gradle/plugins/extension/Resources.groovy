package info.appsense.appstore.gradle.plugins.extension

class Resources {
    def String sourceDir = "store-resources";

    public void isConfigured() {
        if (sourceDir == null || sourceDir.isEmpty()) {
            throw new IllegalArgumentException("SourceDir is not a invalid.");
        }
    }
}
