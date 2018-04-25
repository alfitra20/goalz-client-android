package emse.mobisocial.goalz.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.WindowManager
import emse.mobisocial.goalz.R

class LoginActivity : AppCompatActivity() {

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
    }
}
