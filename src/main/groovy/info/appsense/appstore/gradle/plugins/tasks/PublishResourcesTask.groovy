package info.appsense.appstore.gradle.plugins.tasks

import com.android.build.gradle.api.ApplicationVariant
import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.Image
import com.google.api.services.androidpublisher.model.Listing
import info.appsense.appstore.gradle.plugins.AppStoreDeployExtension
import info.appsense.appstore.gradle.plugins.util.TextUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PublishResourcesTask extends DefaultTask {
    ApplicationVariant applicationVariant
    // visibleForTests
    AndroidPublisher publisher

    @TaskAction
    def upload() {
        AppStoreDeployExtension extension = AppStoreDeployExtension.from(project)
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
        if (publisher == null) {
            publisher = extension.googlePlay.serviceAccount.buildPublisher()
        }
        AndroidPublisher.Edits edits = publisher.edits()
        String editId = edits.insert(packageName, null).execute().getId();

        variantDir.eachDir { File langDir ->
            String lang = langDir.name
            Listing listing = edits.listings().get(packageName, editId, lang).execute()

            boolean dirty = false
            ["fullDescription", "shortDescription", "video", "title"].each {
                File file = new File(langDir, "${it}.txt")
                if (file.exists() && !TextUtils.same(listing.get(it) as String, file.text)) {
                    listing.set(it, file.text.trim())
                    dirty = true
                }
            }
            if (dirty) {
                edits.listings().update(packageName, editId, lang, listing).execute();
            }
            File imagesDir = new File(langDir, "images")
            if (!imagesDir.exists()) {
                logger.lifecycle("Not found " + imagesDir)
                return
            }
            ["icon", "featureGraphic", "promoGraphic", "tvBanner"].each {
                File file = new File(langDir, "${it}.png")
                if (file.exists()) {
                    boolean found = false
                    final String hash = DigestUtils.shaHex(new FileInputStream(file));
                    edits.images().list(packageName, editId, lang, it).execute().getImages().each {
                        if (!found && it.getSha1().equals(hash)) {
                            found = true
                        }
                    }
                    if (!found) {
                        edits.images().upload(packageName, editId, lang, it, new FileContent("image/png", file)).execute()
                    }
                }
            }
            File screenShotsDir = new File(langDir, "screenshots")
            if (!screenShotsDir.exists()) {
                logger.lifecycle("Not found " + screenShotsDir)
                return
            }
            ["phone", "sevenInch", "tenInch", "tv"].each {
                String imageType = "${it}Screenshots"
                File dir = new File(screenShotsDir, it)
                if (!dir.exists()) {
                    logger.error("Not found " + dir)
                    return
                }

                HashedFile[] files = dir.listFiles().collect { new HashedFile(it) }
                if (files.length > 0) {
                    final List<Image> images = edits.images().list(packageName, editId, lang, imageType).execute().getImages();
                    // Remove images not found locally
                    int deleted = images.findAll { Image image ->
                        files.collect().count {
                            image.getSha1().equals(it.hash)
                        } == 0
                    }.each {
                        edits.images().delete(packageName, editId, lang, imageType, it.getId()).execute()
                    }.collect().count { true };

                    // Upload images not found remotely
                    files.findAll { HashedFile file ->
                        images.collect().count {
                            file.hash.equals(it.getSha1())
                        } == 0
                    }.sort {
                        it.file.name
                    }.take(8 - deleted).each {
                        edits.images().upload(packageName, editId, lang, imageType, new FileContent("image/png", it.file)).execute()
                    }
                }
            }
        }
        edits.commit(packageName, editId).execute()
    }

    class HashedFile {
        final File file
        final String hash

        HashedFile(File file) {
            this.file = file
            hash = DigestUtils.shaHex(new FileInputStream(file))
        }
    }
}
