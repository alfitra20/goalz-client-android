package emse.mobisocial.goalz.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.content.res.AppCompatResources
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.activity_onboarding.*

private const val WITHOUT_LOGIN = "without_login"

class OnboardingActivity : AppCompatActivity() {

    private var networkInfo:Boolean = false
    private lateinit var mSnackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val withoutLogin  = preferences.getBoolean(WITHOUT_LOGIN, false)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (withoutLogin || userId!=null){
            val intent = Intent(this, BaseActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_onboarding)
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        networkInfo = isNetworkAvailable()

        if (!networkInfo){
            onboarding_login_button.isEnabled = false
            onboarding_login_button.background = AppCompatResources.getDrawable(this, R.color.greyColor)
            signup_button.isEnabled = false
            signup_button.background = AppCompatResources.getDrawable(this, R.color.greyColor)
            without_login_button.isEnabled = false
            launchSnackbar(application.getString(R.string.onboarding_activity_not_connected))
        }


        onboarding_login_button.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        signup_button.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        without_login_button.setOnClickListener{
            preferences.edit()
                    .putBoolean(WITHOUT_LOGIN, true)
                    .apply()
            val intent = Intent(this, BaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    private fun isNetworkAvailable():Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun launchSnackbar(title: String) {
        mSnackbar = Snackbar.make(onboarding_layout, title, Snackbar.LENGTH_LONG)
        mSnackbar.view.background = AppCompatResources.getDrawable(this, R.color.snackbarErrorColor)
        mSnackbar.show()
    }
}
