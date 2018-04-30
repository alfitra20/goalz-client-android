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
import android.util.Patterns
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var mFirebaseAuth : FirebaseAuth
    private lateinit var mSnackbar: Snackbar
    private var redColor = Color.parseColor("#FF6347")

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.title = "Login"
        var grey_color = Color.parseColor("#CCCCCC")
        supportActionBar?.setBackgroundDrawable(ColorDrawable(grey_color))
        supportActionBar?.elevation = 0F
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = grey_color

        login_button.setOnClickListener {login() }
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
                        launchSnackbar("Login Success")
                        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                        preferences.edit()
                                .putBoolean("without_login", false)
                                .apply()
                        val intent = Intent(this, BaseActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        launchSnackbar("Login Failed, check your credentials")
                    }
                }
            }else{
                launchSnackbar("Wrong Email Format")
            }
        }else if (email =="" && password =="") {
            launchSnackbar("Invalid Fields")
        } else if (password ==""){
            launchSnackbar("Password Required")
        } else{
            launchSnackbar("Email Required")
        }

    }
    private fun launchSnackbar(title: String) {
        mSnackbar = Snackbar.make(login_layout, title, Snackbar.LENGTH_LONG)
        mSnackbar.view.background = ColorDrawable(redColor)
        mSnackbar.show()
    }
}
