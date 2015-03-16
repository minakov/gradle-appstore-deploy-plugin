package info.appsense.appstore.gradle.plugins.gradle

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.testing.auth.oauth2.MockTokenServerTransport
import com.google.api.client.json.GenericJson
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import org.junit.Test

import static org.junit.Assert.assertNotNull

class AndroidPublisherBuilderTest {
    static final JsonFactory JSON_FACTORY = new JacksonFactory()
    static final String SA_KEY_TEXT = "-----BEGIN PRIVATE KEY-----\n" +
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALX0PQoe1igW12i" +
            "kv1bN/r9lN749y2ijmbc/mFHPyS3hNTyOCjDvBbXYbDhQJzWVUikh4mvGBA07qTj79Xc3yBDfKP2IeyYQIFe0t0" +
            "zkd7R9Zdn98Y2rIQC47aAbDfubtkU1U72t4zL11kHvoa0/RuFZjncvlr42X7be7lYh4p3NAgMBAAECgYASk5wDw" +
            "4Az2ZkmeuN6Fk/y9H+Lcb2pskJIXjrL533vrDWGOC48LrsThMQPv8cxBky8HFSEklPpkfTF95tpD43iVwJRB/Gr" +
            "CtGTw65IfJ4/tI09h6zGc4yqvIo1cHX/LQ+SxKLGyir/dQM925rGt/VojxY5ryJR7GLbCzxPnJm/oQJBANwOCO6" +
            "D2hy1LQYJhXh7O+RLtA/tSnT1xyMQsGT+uUCMiKS2bSKx2wxo9k7h3OegNJIu1q6nZ6AbxDK8H3+d0dUCQQDTrP" +
            "SXagBxzp8PecbaCHjzNRSQE2in81qYnrAFNB4o3DpHyMMY6s5ALLeHKscEWnqP8Ur6X4PvzZecCWU9BKAZAkAut" +
            "LPknAuxSCsUOvUfS1i87ex77Ot+w6POp34pEX+UWb+u5iFn2cQacDTHLV1LtE80L8jVLSbrbrlH43H0DjU5AkEA" +
            "gidhycxS86dxpEljnOMCw8CKoUBd5I880IUahEiUltk7OLJYS/Ts1wbn3kPOVX3wyJs8WBDtBkFrDHW2ezth2QJ" +
            "ADj3e1YhMVdjJW5jqwlD/VNddGjgzyunmiZg0uOXsHXbytYmsA545S8KRQFaJKFXYYFo2kOjqOiC1T2cAzMDjCQ" +
            "==\n-----END PRIVATE KEY-----\n";
    private static final String SA_KEY_ID = "key_id";

    @Test
    public void testFromStreamServiceAccount() {
        final String accessToken = "1/MkSJoj1xsli0AccessToken_NKPY2"
        final String serviceAccountId = "36680232662-vrd7ji19qe3nelgchd0ah2csanun6bnr.apps.googleusercontent.com"
        final String serviceAccountEmail = "36680232662-vrd7ji19qgchd0ah2csanun6bnr@developer.gserviceaccount.com"

        MockTokenServerTransport transport = new MockTokenServerTransport()
        transport.addServiceAccount(serviceAccountEmail, accessToken)

        // Write out user file
        GenericJson serviceAccountContents = new GenericJson()
        serviceAccountContents.setFactory(JSON_FACTORY)
        serviceAccountContents.put("client_id", serviceAccountId)
        serviceAccountContents.put("client_email", serviceAccountEmail)
        serviceAccountContents.put("private_key", SA_KEY_TEXT)
        serviceAccountContents.put("private_key_id", SA_KEY_ID)
        serviceAccountContents.put("type", "service_account")

        String json = serviceAccountContents.toPrettyString()
        InputStream serviceAccountStream = new ByteArrayInputStream(json.getBytes())

        GoogleCredential defaultCredential = GoogleCredential.fromStream(serviceAccountStream, transport, JSON_FACTORY);
        assertNotNull(defaultCredential);
    }
}
