package pet.ca.securitypractice.jni


import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pet.ca.securitypractice.R


class JniVerifyFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_jni_verify, container, false)
    }


}
