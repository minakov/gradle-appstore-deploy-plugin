package info.appsense.appstore.gradle.plugins.tasks

import com.android.build.gradle.api.ApplicationVariant
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.ApkListing
import com.google.api.services.androidpublisher.model.Listing
import com.google.api.services.androidpublisher.model.Track
import info.appsense.appstore.gradle.plugins.extension.PluginExtension
import info.appsense.appstore.gradle.plugins.internal.AndroidPublisherFactory
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

class BootstrapResourcesTask extends DefaultTask {
    ApplicationVariant applicationVariant

    @TaskAction
    def generate() {
        def extension = PluginExtension.from(project)
        if (!extension.isConfigured()) {
            return
        }
        Logger log = project.logger
        File variantDir = new File(project.file(extension.resources.sourceDir), applicationVariant.name)
        if (!variantDir.mkdirs() && !variantDir.exists()) {
            log.error("Unable create " + variantDir)
            return
        }
        String packageName = applicationVariant.applicationId

        AndroidPublisher.Edits edits = AndroidPublisherFactory.create(extension.serviceAccount).edits()
        String editId = edits.insert(packageName, null).execute().getId();

        edits.listings().list(packageName, editId).execute().getListings().each { Listing listing ->
            File langDir = new File(variantDir, listing.getLanguage())
            if (!langDir.mkdir() && !langDir.exists()) {
                log.error("Unable create " + langDir)
                return
            }
            ["fullDescription", "shortDescription", "video", "title"].each {
                new File(langDir, "${it}.txt") << listing.get(it)
            }
        }
        edits.tracks().list(packageName, editId).execute().getTracks().each { Track track ->
            String releaseType = track.getTrack().toLowerCase()
            Integer versionCode = track.getVersionCodes().sort().last()
            edits.apklistings().list(packageName, editId, versionCode).execute().getListings().each { ApkListing listing ->
                File langDir = new File(variantDir, listing.getLanguage())
                if (!langDir.mkdir() && !langDir.exists()) {
                    log.error("Unable create " + langDir)
                    return
                }
                new File(langDir, "recentChanges${releaseType.capitalize()}.txt") << listing.getRecentChanges()
            }
        }

        String lang = edits.details().get(packageName, editId).execute().getDefaultLanguage();
        File defaultDir = new File(variantDir, lang)
        if (!defaultDir.mkdir() && !defaultDir.exists()) {
            return
        }
        variantDir.eachDir { File langDir ->
            ["fullDescription", "shortDescription", "video", "title"].each {
                File file = new File(langDir, "${it}.txt")
                file.exists() || file << ""
            }
            ["alpha", "beta", "production"].each {
                File file = new File(langDir, "recentChanges${it.capitalize()}.txt")
                file.exists() || file << ""
            }

            File imagesDir = new File(langDir, "images")
            if (!imagesDir.mkdir() && !imagesDir.exists()) {
                log.error("Unable create " + imagesDir)
                return
            }
            ["icon", "featureGraphic", "promoGraphic", "tvBanner"].each {
                File file = new File(imagesDir, "${it}.png")
                file.exists() || file << ""
            }
            File screenShotsDir = new File(langDir, "screenshots")
            if (!screenShotsDir.mkdir() && !screenShotsDir.exists()) {
                log.error("Unable create " + screenShotsDir)
                return
            }
            ["phone", "sevenInch", "tenInch", "tv"].each {
                File dir = new File(screenShotsDir, it)
                if (!dir.exists()) {
                    if (!dir.mkdir() && !dir.exists()) {
                        log.error("Unable create " + dir)
                        return
                    }
                    (1..8).each {
                        new File(dir, "${it}.png") << ""
                    }
                }
            }
        }

        edits.commit(packageName, editId).execute()
    }
}
