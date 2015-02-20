package info.appsense.appstore.gradle.plugins.gradle

import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.*
import info.appsense.appstore.gradle.plugins.tasks.PublishApplicationTask
import org.gradle.api.Project
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

import static org.mockito.Matchers.*
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.verify
import static org.mockito.MockitoAnnotations.initMocks

/**
 * Created by vladimir.minakov on 17.02.15.
 */
class PublishApplicationTaskTest {
    @Mock
    AndroidPublisher publisher
    @Mock
    AndroidPublisher.Edits edits
    @Mock
    AndroidPublisher.Edits.Insert insert
    @Mock
    AndroidPublisher.Edits.Commit commit
    // AppEdit is final and not mockable
    AppEdit appEdit = new AppEdit().setId("1234567890")

    @Mock
    AndroidPublisher.Edits.Listings listings
    @Mock
    AndroidPublisher.Edits.Listings.List listingsList
    // ListingsListResponse is final and not mockable
    ListingsListResponse listingsResponse = new ListingsListResponse()

    @Mock
    AndroidPublisher.Edits.Tracks tracks
    @Mock
    AndroidPublisher.Edits.Tracks.List tracksList
    // TracksListResponse is final and not mockable
    TracksListResponse tracksListResponse = new TracksListResponse()
    @Mock
    AndroidPublisher.Edits.Tracks.Update tracksUpdate
    // Track is final and not mockable
    Track track = new Track()

    @Mock
    AndroidPublisher.Edits.Apklistings apkListings
    @Mock
    AndroidPublisher.Edits.Apklistings.List apkListingsList
    // ApkListingsListResponse is final and not mockable
    ApkListingsListResponse apkListingsListResponse = new ApkListingsListResponse()

    @Mock
    AndroidPublisher.Edits.Details details
    @Mock
    AndroidPublisher.Edits.Details.Get detailsGet
    // ListingsListResponse is final and not mockable
    AppDetails appDetails = new AppDetails()

    @Mock
    AndroidPublisher.Edits.Apks apks
    @Mock
    AndroidPublisher.Edits.Apks.List apksList
    // ApksListResponse is final and not mockable
    ApksListResponse apksListResponse = new ApksListResponse()
    @Mock
    AndroidPublisher.Edits.Apks.Upload apksUpload
    // Apk is final and not mockable
    Apk apk

    @Before
    public void setup() {
        initMocks(this)

        doReturn(edits).when(publisher).edits()
        doReturn(insert).when(edits).insert(anyString(), any(AppEdit.class))
        doReturn(appEdit).when(insert).execute()

        doReturn(commit).when(edits).commit(anyString(), anyString())
        doReturn(appEdit).when(commit).execute()

        doReturn(listings).when(edits).listings()
        doReturn(listingsList).when(listings).list(anyString(), anyString())
        doReturn(listingsResponse).when(listingsList).execute()

        doReturn(tracks).when(edits).tracks()
        doReturn(tracksList).when(tracks).list(anyString(), anyString())
        doReturn(tracksUpdate).when(tracks).update(anyString(), anyString(), anyString(), any(Track))
        doReturn(tracksListResponse).when(tracksList).execute()
        doReturn(track).when(tracksUpdate).execute()

        doReturn(apkListings).when(edits).apklistings()
        doReturn(apkListingsList).when(apkListings).list(anyString(), anyString(), anyInt())
        doReturn(apkListingsListResponse).when(apkListingsList).execute()

        doReturn(apks).when(edits).apks()
        doReturn(apksList).when(apks).list(anyString(), anyString())
        doReturn(apksUpload).when(apks).upload(anyString(), anyString(), any(FileContent))
        doReturn(apksListResponse).when(apksList).execute()
        doReturn(apk).when(apksUpload).execute()

        doReturn(details).when(edits).details()
        doReturn(detailsGet).when(details).get(anyString(), anyString())
        doReturn(appDetails).when(detailsGet).execute()
    }

    @Test
    public void testApplicationId() {
        Project project = ProjectFactory.build()
        project.appStoreDeploy {
            googlePlay {
                serviceAccount {
                    email = 'email'
                    storeFile = project.file('key.p12')
                }
            }
        }
        project.evaluate()

        PublishApplicationTask task = project.tasks.publishGooglePlayBetaApplicationRelease as PublishApplicationTask
        task.publisher = publisher
        task.upload()

        verify(edits).insert("com.example", null)
    }
}
