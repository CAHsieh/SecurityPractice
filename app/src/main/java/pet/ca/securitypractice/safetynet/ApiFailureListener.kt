package pet.ca.securitypractice.safetynet

import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.OnFailureListener

object ApiFailureListener : OnFailureListener {
    override fun onFailure(e: Exception) {

        if (e is ApiException) {
            // An error with the Google Play Services API contains some
            // additional details.
            Log.e(
                "SafetyNetFailure",
                "Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}"
            )

            // Note: If the status code, s.statusCode,
            // is SafetyNetstatusCode.SAFE_BROWSING_API_NOT_INITIALIZED,
            // you need to call initSafeBrowsing(). It means either you
            // haven't called initSafeBrowsing() before or that it needs
            // to be called again due to an internal error.
        } else {
            // A different, unknown type of error occurred.
            Log.e("SafetyNetFailure", "Error: ${e.message}")
        }
    }

}