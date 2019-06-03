package pet.ca.securitypractice

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.activity_main.*

const val PLAY_SERVICES_RESOLUTION_REQUEST = 0x01
const val PLAY_SERVICES_DISABLE = 0x02

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val apiAvailability = GoogleApiAvailability.getInstance()
        if (apiAvailability.isGooglePlayServicesAvailable(this, 13000000)
            == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
        ) {
            when (requestCode) {
                PLAY_SERVICES_RESOLUTION_REQUEST -> {
                    apiAvailability.showErrorDialogFragment(
                        this, ConnectionResult.SERVICE_DISABLED, PLAY_SERVICES_DISABLE
                    )
                }
                PLAY_SERVICES_DISABLE -> {
                    apiAvailability.showErrorDialogFragment(
                        this,
                        ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,
                        PLAY_SERVICES_RESOLUTION_REQUEST
                    )
                }
            }
        }
    }

    public fun showProgress() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        progressFrame.visibility = View.VISIBLE
    }

    public fun dismissProgress() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressFrame.visibility = View.GONE
    }
}
