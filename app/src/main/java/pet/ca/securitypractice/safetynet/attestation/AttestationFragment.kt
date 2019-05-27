package pet.ca.securitypractice.safetynet.attestation


import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Slide
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.common.io.BaseEncoding
import kotlinx.android.synthetic.main.fragment_attestation.*
import pet.ca.securitypractice.R

class AttestationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedElementEnterTransition = ChangeBounds().apply {
            duration = 300
        }
        enterTransition = Slide().apply {
            duration = 500
        }
        return inflater.inflate(R.layout.fragment_attestation, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val attestation = Attestation()

        nonce.text = BaseEncoding.base16().encode(attestation.nonce)

        refreshNonce.setOnClickListener {
            attestation.refreshNonce()
            nonce.text = BaseEncoding.base16().encode(attestation.nonce)
        }

        start.setOnClickListener {
            attestation.start(view.context) { stmt ->
                showStmtToLog(stmt)
            }
        }
    }

    private fun showStmtToLog(stmt: AttestationStatement?) {
        if (stmt == null) {
            Log.e(TAG_ATTESTATION, "Failure: Failed to parse and verify the attestation statement.")
        } else {
            Log.e(
                TAG_ATTESTATION,
                "Successfully verified the attestation statement. The content is:"
            )

            Log.e(TAG_ATTESTATION, "Nonce: " + stmt.nonce)
            Log.e(TAG_ATTESTATION, "Timestamp: " + stmt.timestampMs + " ms")
            Log.e(TAG_ATTESTATION, "APK package name: " + stmt.apkPackageName)
            Log.e(TAG_ATTESTATION, "APK digest SHA256: " + stmt.apkDigestSha256)
            stmt.apkCertificateDigestSha256?.forEach {
                Log.e(TAG_ATTESTATION, "APK certificate digest SHA256: $it")
            }
            Log.e(TAG_ATTESTATION, "CTS profile match: " + stmt.isCtsProfileMatch)
            Log.e(TAG_ATTESTATION, "Has basic integrity: " + stmt.hasBasicIntegrity())
            Log.e(
                TAG_ATTESTATION, "** This sample only shows how to verify the authenticity of an attestation response. Next, you must check that the server response matches the request by comparing the nonce, package name, timestamp and digest."
            )
        }
    }
}
