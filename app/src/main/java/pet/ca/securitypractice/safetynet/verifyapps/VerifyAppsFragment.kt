package pet.ca.securitypractice.safetynet.verifyapps


import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_verify_apps.*
import pet.ca.securitypractice.R


class VerifyAppsFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_verify_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isVerifyBtn.setOnClickListener {

            isVerifyResult.text = ""

            SafetyNet.getClient(it.context)
                .isVerifyAppsEnabled
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isVerifyResult.text = task.result?.isVerifyAppsEnabled.toString()
                    } else {
                        isVerifyResult.text = "A general error occurred."
                    }
                }
        }

        enableVerifyBtn.setOnClickListener {

            enableVerifyResult.text = ""

            SafetyNet.getClient(it.context)
                .enableVerifyApps()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result?.isVerifyAppsEnabled == true) {
                            enableVerifyResult.text =
                                "Success! The user gave consent to enable the Verify Apps feature."
                        } else {
                            enableVerifyResult.text =
                                "Failed. The user didn't give consent to enable the Verify Apps feature."
                        }
                    } else {
                        enableVerifyResult.text = "A general error occurred."
                    }
                }

        }

        listHarmfulAppsBtn.setOnClickListener {
            harmfulAppsLayout.removeAllViews()

            SafetyNet.getClient(it.context)
                .listHarmfulApps()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result
                        result?.run {
                            val scanTimeMs = result.lastScanTimeMs
                            val appList = result.harmfulAppsList

                            listTimeResult.text = "$scanTimeMs ms"

                            if (appList.isNotEmpty()) {
                                for (harmfulApp in appList) {
                                    val tv = TextView(it.context)
                                    tv.text = "--\n" +
                                            "App Name: ${harmfulApp.apkPackageName}\n" +
                                            "SHA-256: ${harmfulApp.apkSha256}\n" +
                                            "Category: ${harmfulApp.apkCategory}"
                                    harmfulAppsLayout.addView(tv)
                                }
                            } else {
                                val tv = TextView(it.context)
                                tv.text = "There are no known potentially harmful apps installed."
                                harmfulAppsLayout.addView(tv)
                            }

                        }
                    } else {
                        Snackbar.make(
                            view,
                            "An error occurred. Call isVerifyAppsEnabled() to ensure that the user has consented.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }
}
