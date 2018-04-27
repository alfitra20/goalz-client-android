package emse.mobisocial.goalz.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mFirebaseAuth : FirebaseAuth

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
        if(email_text.text.toString()!=""&&password_text.text.toString()!=""){
            if(Patterns.EMAIL_ADDRESS.matcher(email_text.text).matches()){

                mFirebaseAuth = FirebaseAuth.getInstance()
                mFirebaseAuth.signInWithEmailAndPassword(email_text.text.toString(), password_text.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("SIGNIN", "login success")
                        Toast.makeText(this, "Login Success", Toast.LENGTH_LONG).show()
                        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                        preferences.edit()
                                .putBoolean("without_login", false)
                                .apply()
                        val intent = Intent(this, BaseActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Log.d("SIGNIN", "login fail")
                        Toast.makeText(this, "Login Failed, check your credentials", Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(this, "Wrong Email Format", Toast.LENGTH_SHORT).show()
            }
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(email_text.text).matches()){
                Toast.makeText(this, "Wrong Email Format", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "Invalid Fields", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
