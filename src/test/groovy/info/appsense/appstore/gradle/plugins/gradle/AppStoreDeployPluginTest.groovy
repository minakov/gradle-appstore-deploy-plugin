package info.appsense.appstore.gradle.plugins.gradle

import org.gradle.api.Project
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

/**
 * Created by vladimir.minakov on 17.02.15.
 */
class AppStoreDeployPluginTest {

    @Test(expected = PluginApplicationException.class)
    public void testThrowsOnLibraryProjects() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'com.android.library'
        project.apply plugin: 'appstore.deploy'
    }

    @Test
    public void testCreatesFlavorTasks() {
        Project project = ProjectFactory.build()
        project.android.productFlavors {
            free
        }
        project.evaluate()
        final variant = project.android.applicationVariants[1]

        assertNotNull(project.tasks.generateGooglePlayResourcesFreeRelease)
        assertEquals(project.tasks.generateGooglePlayResourcesFreeRelease.applicationVariant, variant)

        assertNotNull(project.tasks.publishGooglePlayResourcesFreeRelease)
        assertEquals(project.tasks.publishGooglePlayResourcesFreeRelease.applicationVariant, variant)

        assertNotNull(project.tasks.publishGooglePlayAlphaApplicationFreeRelease)
        assertEquals(project.tasks.publishGooglePlayAlphaApplicationFreeRelease.releaseType, "alpha")
        assertEquals(project.tasks.publishGooglePlayAlphaApplicationFreeRelease.applicationVariant, variant)

        assertNotNull(project.tasks.publishGooglePlayBetaApplicationFreeRelease)
        assertEquals(project.tasks.publishGooglePlayBetaApplicationFreeRelease.releaseType, "beta")
        assertEquals(project.tasks.publishGooglePlayBetaApplicationFreeRelease.applicationVariant, variant)

        assertNotNull(project.tasks.publishGooglePlayProductionApplicationFreeRelease)
        assertEquals(project.tasks.publishGooglePlayProductionApplicationFreeRelease.releaseType, "production")
        assertEquals(project.tasks.publishGooglePlayProductionApplicationFreeRelease.applicationVariant, variant)
    }
}
