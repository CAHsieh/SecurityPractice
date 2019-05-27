package pet.ca.securitypractice


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiAvailability = GoogleApiAvailability.getInstance()

        /**
         * On devices running Google Play Services v13.0 and above,
         * the SafetyNet Attestation API also supports app-restricted API keys.
         * This feature reduces the risk of accidental or unauthorized usage of quota-restricted API keys.
         * */

        when (apiAvailability.isGooglePlayServicesAvailable(activity, 13000000)) {
            ConnectionResult.SUCCESS -> {
                btnAttestation.setOnClickListener {
                    //                    val attestation = Attestation()
//                    attestation.start(it.context)
                    val extras = FragmentNavigatorExtras(btnAttestation to "transition_title")
                    findNavController().navigate(
                        R.id.action_mainFragment_to_attestationFragment,
                        null, null, extras
                    )
                }
            }
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> {
                apiAvailability.showErrorDialogFragment(
                    activity,
                    ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,
                    PLAY_SERVICES_RESOLUTION_REQUEST
                )
            }
        }

    }
}