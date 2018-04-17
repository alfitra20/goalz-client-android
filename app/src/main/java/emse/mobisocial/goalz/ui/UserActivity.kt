package emse.mobisocial.goalz.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import emse.mobisocial.goalz.R


class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val actionbar = supportActionBar
        actionbar?.title = "User1 Profile"

        //actionbar.setDefaultDisplayHomeAsUpEnabled(true)
    }

}
