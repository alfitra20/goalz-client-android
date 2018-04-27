package emse.mobisocial.goalz.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : AppCompatActivity() {

    private lateinit var mFirebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val withoutLogin  = preferences.getBoolean("without_login", false)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (withoutLogin || userId!=null){
            val intent = Intent(this, BaseActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_onboarding)
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        login_button.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        signup_button.setOnClickListener{

        }
        without_login_button.setOnClickListener{
            preferences.edit()
                    .putBoolean("without_login", true)
                    .apply()
            val intent = Intent(this, BaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
