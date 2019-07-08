package pet.ca.securitypractice.safetynet.safebrowsing


import android.content.Context
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.safetynet.SafeBrowsingThreat
import com.google.android.gms.safetynet.SafetyNet
import kotlinx.android.synthetic.main.fragment_safe_browsing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pet.ca.securitypractice.BuildConfig
import pet.ca.securitypractice.R
import pet.ca.securitypractice.safetynet.ApiFailureListener
/**
 * Test Links from: https://testsafebrowsing.appspot.com/
 * */
class SafeBrowsingFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val urls =
        arrayOf(
            "https://www.google.com",
            "https://testsafebrowsing.appspot.com/s/phishing.html",
            "https://testsafebrowsing.appspot.com/s/malware.html",
            "https://testsafebrowsing.appspot.com/s/malware_in_iframe.html",
            "https://testsafebrowsing.appspot.com/s/unwanted.html",
            "https://testsafebrowsing.appspot.com/s/image_small.html",
            "https://testsafebrowsing.appspot.com/s/trick_to_bill.html"
        )

    private var selectUrl = urls[0]

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
        return inflater.inflate(R.layout.fragment_safe_browsing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        urlSpinner.adapter =
            ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, urls)
        urlSpinner.onItemSelectedListener = this

        check.setOnClickListener {
            resultLayout.removeAllViews()
            checkUrl(it.context)
        }
    }

    private fun checkUrl(context: Context) {
        GlobalScope.launch(Dispatchers.Main) {

            val job = GlobalScope.async {
                SafetyNet.getClient(context).lookupUri(
                    selectUrl,
                    BuildConfig.API_KEY,
                    SafeBrowsingThreat.TYPE_POTENTIALLY_HARMFUL_APPLICATION,
                    SafeBrowsingThreat.TYPE_SOCIAL_ENGINEERING
                ).addOnSuccessListener {
                    // Indicates communication with the service was successful.
                    // Identify any detected threats.
                    if (it.detectedThreats.isEmpty()) {
                        addTextView("No threats found.")
                    } else {
                        addTextView("Threats found:")
                        for (threat in it.detectedThreats) {
                            when (threat.threatType) {
                                SafeBrowsingThreat.TYPE_SOCIAL_ENGINEERING -> {
                                    addTextView("TYPE_SOCIAL_ENGINEERING")
                                }
                                SafeBrowsingThreat.TYPE_POTENTIALLY_HARMFUL_APPLICATION -> {
                                    addTextView("TYPE_POTENTIALLY_HARMFUL_APPLICATION")
                                }
                            }
                        }
                    }
                }.addOnFailureListener(ApiFailureListener)
            }

            job.await()
        }
    }

    private fun addTextView(text: String) {
        context?.run {
            val tv = TextView(context)
            tv.text = text

            resultLayout.addView(tv)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectUrl = urls[position]
    }
}
