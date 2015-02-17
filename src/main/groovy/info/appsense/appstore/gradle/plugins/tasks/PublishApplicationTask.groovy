package info.appsense.appstore.gradle.plugins.tasks

import com.android.build.gradle.api.ApplicationVariant
import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.Apk
import com.google.api.services.androidpublisher.model.Track
import info.appsense.appstore.gradle.plugins.extension.PluginExtension
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PublishApplicationTask extends DefaultTask {
    ApplicationVariant applicationVariant
    String releaseType

    @TaskAction
    def upload() {
        PluginExtension extension = PluginExtension.from(project)
        try {
            extension.isConfigured()
        } catch (IllegalArgumentException e) {
            logger.warn(e.message)
            return
        }
        File variantDir = new File(project.file(extension.resources.sourceDir), applicationVariant.name)
        if (!variantDir.exists()) {
            logger.error("Unable open " + variantDir)
            return
        }
        String packageName = applicationVariant.applicationId

        AndroidPublisher.Edits edits = extension.getGooglePlay().getServiceAccount().buildPublisher().edits()
        String editId = edits.insert(packageName, null).execute().getId();

        List<Apk> apks = edits.apks().list(packageName, editId).execute().getApks()
        applicationVariant.outputs.findAll {
            def versionCode = it.getVersionCode()
            apks.count {
                it.getVersionCode().equals(versionCode)
            } == 0
        }.findAll {
            FilenameUtils.isExtension(it.outputFile.name, "apk")
        }.each {
            FileContent fileContent = new FileContent("application/vnd.android.package-archive", it.outputFile)
            edits.apks().upload(packageName, editId, fileContent).execute()
        }

        // Remove current versionCodes from the existing track
        edits.tracks().list(packageName, editId).execute().getTracks().findAll { Track track ->
            track.getVersionCodes().findAll { Integer versionCode ->
                applicationVariant.outputs.count {
                    it.getVersionCode() == versionCode
                } > 0
            }
        }.each { Track track ->
            track.setVersionCodes(track.getVersionCodes().findAll { Integer versionCode ->
                applicationVariant.outputs.count {
                    it.getVersionCode() == versionCode
                } == 0
            }.collect());
            edits.tracks().update(packageName, editId, track.getTrack(), track).execute();
        }

        Track track = new Track().setTrack(releaseType).setVersionCodes(applicationVariant.outputs.collect {
            it.getVersionCode()
        });
        edits.tracks().update(packageName, editId, track.getTrack(), track).execute();

        edits.commit(packageName, editId).execute()
    }

}
