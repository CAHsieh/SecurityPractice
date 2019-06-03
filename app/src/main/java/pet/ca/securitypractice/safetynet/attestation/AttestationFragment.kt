package pet.ca.securitypractice.safetynet.attestation


import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.common.io.BaseEncoding
import kotlinx.android.synthetic.main.fragment_attestation.*
import pet.ca.securitypractice.MainActivity
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
            (activity as MainActivity).showProgress()

            attestation.start(view.context) { stmt ->
                showStmtToLog(stmt)
                (activity as MainActivity).dismissProgress()
            }
        }
    }

    private fun showStmtToLog(stmt: AttestationStatement?) {
        if (stmt == null) {
            Snackbar.make(
                frame,
                "Failure: Failed to parse and verify the attestation statement.",
                Snackbar.LENGTH_SHORT
            ).show()
        } else {

            Snackbar.make(
                frame,
                "Successfully verified the attestation statement",
                Snackbar.LENGTH_SHORT
            ).show()

            resultBackNonce.text = stmt.nonce
            resultTimestamp.text = stmt.timestampMs.toString().plus(" ms")
            resultPackageName.text = stmt.apkPackageName
            resultDigestSHA256.text = stmt.apkDigestSha256

            certificateLayout.removeAllViews()
            stmt.apkCertificateDigestSha256?.forEach {
                val textView = TextView(context)
                textView.text = it
                certificateLayout.addView(textView)
            }

            resultCTSProfile.text = stmt.isCtsProfileMatch.toString()
            resultBasic.text = stmt.hasBasicIntegrity().toString()
        }
    }
}
