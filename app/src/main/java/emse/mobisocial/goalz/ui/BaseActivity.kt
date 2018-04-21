package emse.mobisocial.goalz.ui

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.activity_base.*
import android.animation.Animator
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator


open class BaseActivity : AppCompatActivity() {

    // Temporary for deciding which navigation menu and header to be shown
    // If the user use the app without login
    // it will use without_login_base_drawer menu
    private var loggedIn =  true
    private lateinit var mContext:Context
    private lateinit var toggle: ActionBarDrawerToggle

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)
        Log.d("suppor", supportActionBar.toString())

        mContext = this@BaseActivity

        setInitialFragment()
        setUpNav()
        toggle.syncState()
        ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),1)
    }

    private fun setInitialFragment() {
        if (loggedIn) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.content_frame, GoalsFragment())
            transaction.commit()
            supportActionBar?.title = getString(R.string.app_bar_goals)

        } else {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.content_frame, ExploreFragment())
            transaction.commit()
            supportActionBar?.title = getString(R.string.app_bar_explore)
        }
    }

    private fun setUpNav() {
        toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout?.addDrawerListener(toggle)

        //Initialize profile menu from navigation view in Tabbed activity
        var header = nav_view.getHeaderView(0)
        var sidebarNickname : TextView = header.findViewById(R.id.sidebar_nickname)
        var profileImage : ImageView = header.findViewById(R.id.profile_image)

        nav_view.itemIconTintList = null

        // Initialize default views
        // When the user logged in the profile picture and nickname will redirect to user's profile
        // if user using the app without login, it will only show Goalz there instead of nickname (as for now)
        if (loggedIn) {
            sidebarNickname.setOnClickListener {
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
            }
            profileImage.setOnClickListener {
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
            }
            addGoalButton.setOnClickListener {
                val intent = Intent(this, CreateGoalActivity::class.java)
                startActivity(intent)
            }
            addResourceButton.setOnClickListener {
                val intent = Intent(this, CreateResourceActivity::class.java)
                startActivity(intent)
            }
        }
        else {
            nav_view.menu.clear()
            nav_view.inflateMenu(R.menu.without_login_base_drawer)
            sidebarNickname.text = getString(R.string.app_name)
            fab.hideMenuButton(true)
        }

        nav_view.setNavigationItemSelectedListener { item ->
            var displayedFragment: Fragment = ExploreFragment()
            var actionBarTitle = getString(R.string.app_bar_explore)
            when (item.itemId) {

            // Available for user who use the app without login
                R.id.nav_explore -> {
                    displayedFragment = ExploreFragment()
                    actionBarTitle = getString(R.string.app_bar_explore)
                }

            // Available only for user who login
                R.id.nav_goals -> {
                    displayedFragment = GoalsFragment()
                    actionBarTitle = getString(R.string.app_bar_goals)
                }
                R.id.nav_library -> {
                    displayedFragment = UsersLibraryFragment()
                    actionBarTitle = getString(R.string.app_bar_users_library)
                }
                R.id.nav_timeline -> {

                }
                R.id.nav_setting -> {

                }
                R.id.nav_logout -> {

                }

            // Without login menu
                R.id.nav_login -> {

                }
                R.id.nav_signup -> {

                }
            }
            item.setChecked(true)

            // Set selected/ default fragment and appbarTitle
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.content_frame, displayedFragment)
            transaction.commit()
            supportActionBar?.title = actionBarTitle

            drawer_layout.closeDrawer(GravityCompat.START)

            true
        }
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        if (drawer_layout != null && drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.base, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }
}