package emse.mobisocial.goalz

import android.os.Bundle
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_base_basic.*

class UserActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_basic)
        setSupportActionBar(toolbar_basic)
        val actionbar = supportActionBar
        actionbar?.title = "User1 Profile"

        //Initialize the Navigation view and menu + floating button
        setFabBasic()

        fab_basic.hide()

        val contentFrameLayout = content_frame as FrameLayout
        layoutInflater.inflate(R.layout.activity_user, contentFrameLayout)
    }

}
