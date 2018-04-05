package emse.mobisocial.goalz.ui

import android.os.Bundle
import android.widget.FrameLayout
import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.activity_base_basic.*

class UsersLibraryActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_basic)
        setSupportActionBar(toolbar_basic)
        val actionbar = supportActionBar
        actionbar?.title = getString(R.string.app_bar_users_library)

        //Initialize the Navigation view and menu + floating button
        setFabBasic()

        val contentFrameLayout = content_frame as FrameLayout
        layoutInflater.inflate(R.layout.activity_users_library, contentFrameLayout)
    }

}
