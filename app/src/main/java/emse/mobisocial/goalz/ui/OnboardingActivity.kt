package emse.mobisocial.goalz.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        var onboardingSlideAdapter = OnboardingSlideAdapter(this)

        onboardingSlideView.adapter = onboardingSlideAdapter

    }
}
