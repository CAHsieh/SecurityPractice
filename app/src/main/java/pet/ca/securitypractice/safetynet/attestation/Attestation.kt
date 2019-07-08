package pet.ca.securitypractice.safetynet.attestation

import android.content.Context
import android.util.Log
import com.google.android.gms.safetynet.SafetyNet
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.json.webtoken.JsonWebSignature
import com.google.common.io.BaseEncoding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pet.ca.securitypractice.BuildConfig
import pet.ca.securitypractice.safetynet.ApiFailureListener
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.security.cert.X509Certificate

const val TAG_ATTESTATION = "Attestation"

//SafetyNet Attestation API 用於檢查設備完整性
class Attestation {

    lateinit var nonce: ByteArray

    init {
        refreshNonce()
    }

    //    listener: OnSuccessListener<SafetyNetApi.AttestationResponse>
    fun start(
        context: Context, callback: (stmt: AttestationStatement?) -> Unit
    ) {

        GlobalScope.launch(Dispatchers.Main) {
            val job = this.async {

                Log.e(TAG_ATTESTATION, BaseEncoding.base16().encode(nonce))


                SafetyNet.getClient(context)
                    .attest(nonce, BuildConfig.API_KEY)
                    .addOnSuccessListener { response ->
                        // Indicates communication with the service was successful.
                        // Use response.getJwsResult() to get the result data.
                        Log.e(TAG_ATTESTATION, "Result: " + response.jwsResult)


                        val stmt = parseAndVerify(response.jwsResult)
                        callback.invoke(stmt)
                    }
                    .addOnFailureListener (ApiFailureListener)

            }

            job.await()
        }

    }

    fun refreshNonce() {
        val length = SecureRandom().nextInt(16) + 16

        nonce = ByteArray(length)
        for (i in 0 until length) {
            nonce[i] = SecureRandom().nextInt(255).toByte()
        }
    }

    private fun parseAndVerify(signedAttestationStatment: String): AttestationStatement? {
        val jws: JsonWebSignature

        try {
            jws = JsonWebSignature.parser(JacksonFactory.getDefaultInstance())
                .setPayloadClass(AttestationStatement::class.java)
                .parse(signedAttestationStatment)
        } catch (e: IOException) {
            Log.e(TAG_ATTESTATION, "Failure: not valid JWS format.")
            return null
        }

        // Verify the signature of the JWS and retrieve the signature certificate.
        val cert: X509Certificate
        try {
            cert = jws.verifySignature()
            if (cert == null) {
                Log.e(TAG_ATTESTATION, "Failure: Signature verification failed.")
                return null
            }
        } catch (e: GeneralSecurityException) {
            Log.e(
                TAG_ATTESTATION,
                "Failure: Error during cryptographic verification of the JWS signature."
            )
            return null
        }

        // Verify the hostname of the certificate.
//        try {
//            DefaultHostnameVerifier().verify("<Host Domain>", cert)
//        } catch (e: SSLException) {
//            Log.e(TAG_ATTESTATION, "Failure: Certificate isn't issued for the hostname <Host Domain>")
//            return null
//        }

        return jws.payload as AttestationStatement
    }
}