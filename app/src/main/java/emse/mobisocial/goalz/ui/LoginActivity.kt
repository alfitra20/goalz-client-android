package emse.mobisocial.goalz.ui

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.content.res.AppCompatResources
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.activity_login.*

private const val WITHOUT_LOGIN = "without_login"

class LoginActivity : AppCompatActivity() {

    private lateinit var mFirebaseAuth : FirebaseAuth
    private lateinit var mSnackbar: Snackbar

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.title = application.getString(R.string.login_activity_appbar_title)
        supportActionBar?.elevation = 0F
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        onboarding_login_button.setOnClickListener {login() }
    }

    private fun login(){
        val email = email_text.text.toString().trim()
        val password = password_text.text.toString().trim()
        val checkEmail = Patterns.EMAIL_ADDRESS.matcher(email_text.text).matches()
        if(email !=""&& password !=""){
            if(checkEmail){
                mFirebaseAuth = FirebaseAuth.getInstance()
                mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(application, application.getString(R.string.login_activity_login_success),
                                Toast.LENGTH_LONG).show()
                        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                        preferences.edit()
                                .putBoolean(WITHOUT_LOGIN, false)
                                .apply()
                        val intent = Intent(this, BaseActivity::class.java)

                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        launchSnackbar(application.getString(R.string.login_activity_login_failed))
                    }
                }
            }else{
                launchSnackbar(application.getString(R.string.login_activity_wrong_email_format_snackbar))
            }
        }else if (email =="" && password =="") {
            launchSnackbar(application.getString(R.string.login_activity_invalid_fields_snackbar))
        } else if (password ==""){
            launchSnackbar(application.getString(R.string.login_activity_password_required_snackbar))
        } else{
            launchSnackbar(application.getString(R.string.login_activity_email_required_snackbar))
        }

    }
    private fun launchSnackbar(title: String) {
        mSnackbar = Snackbar.make(login_layout, title, Snackbar.LENGTH_LONG)
        mSnackbar.view.background = AppCompatResources.getDrawable(this, R.color.snackbarErrorColor)
        mSnackbar.show()
    }
}
