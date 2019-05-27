package pet.ca.securitypractice.safetynet.attestation;

import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.util.Base64;
import com.google.api.client.util.Key;
import com.google.common.io.BaseEncoding;

@SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
public class AttestationStatement extends JsonWebSignature.Payload {
    /**
     * Embedded nonce sent as part of the request.
     */
    @Key
    private String nonce;

    /**
     * Timestamp of the request.
     */
    @Key
    private long timestampMs;

    /**
     * Package name of the APK that submitted this request.
     */
    @Key
    private String apkPackageName;

    /**
     * Digest of certificate of the APK that submitted this request.
     */
    @Key
    private String[] apkCertificateDigestSha256;

    /**
     * Digest of the APK that submitted this request.
     */
    @Key
    private String apkDigestSha256;

    /**
     * The device passed CTS and matches a known profile.
     */
    @Key
    private boolean ctsProfileMatch;


    /**
     * The device has passed a basic integrity test, but the CTS profile could not be verified.
     */
    @Key
    private boolean basicIntegrity;

    public String getNonce() {
        return BaseEncoding.base16().encode(Base64.decodeBase64(nonce));
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public String getApkDigestSha256() {
        return BaseEncoding.base16().encode(Base64.decodeBase64(apkDigestSha256));
    }

    public String[] getApkCertificateDigestSha256() {
        String[] certs = new String[apkCertificateDigestSha256.length];
        for (int i = 0; i < apkCertificateDigestSha256.length; i++) {
            certs[i] = BaseEncoding.base16().encode(Base64.decodeBase64(apkCertificateDigestSha256[i]));
        }
        return certs;
    }

    public boolean isCtsProfileMatch() {
        return ctsProfileMatch;
    }

    public boolean hasBasicIntegrity() {
        return basicIntegrity;
    }
}