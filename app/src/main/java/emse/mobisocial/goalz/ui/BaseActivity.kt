package emse.mobisocial.goalz.ui

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
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
import emse.mobisocial.goalz.ui.resource_library.ResourceLibraryFragment
import kotlinx.android.synthetic.main.activity_base.*
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.content.res.AppCompatResources
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.util.IDialogResultListener

open class BaseActivity : AppCompatActivity(), ResourceLibraryFragment.OnFragmentInteractionListener, IDialogResultListener {

    private var loggedInUserId : String? = null
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mSnackbar: Snackbar

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)

    override fun onFragmentInteraction(uri: Uri) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)
        Log.d("support", supportActionBar.toString())

        loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid

        if(loggedInUserId == null) {
            mSnackbar = Snackbar.make(base_layout,
                    "You are not logged in", Snackbar.LENGTH_LONG)
            val loginIntent = Intent(this, LoginActivity::class.java)
            mSnackbar.setAction("Login") { startActivity(loginIntent) }
        }

        val receivedRequest:Int? = intent.getIntExtra("position", 0)
        if (receivedRequest != null) {
            setRequestedFragment(receivedRequest)
        }else{
            setInitialFragment()
        }

        setUpNav()

        toggle.syncState()

        ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),1)
    }

    private fun setRequestedFragment(position: Int){
        var fragment : Fragment? = null
        var title : String? = null
        when (position) {
            0 -> {
                fragment = MyGoalsFragment()
                title = getString(R.string.app_bar_goals)
            }
            2 -> {
                fragment = ResourceLibraryFragment()
                title = getString(R.string.app_bar_users_library)
            }
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, fragment)
        transaction.commit()
        supportActionBar?.title = title
    }

    private fun setInitialFragment() {
        if (loggedInUserId != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.content_frame, MyGoalsFragment())
            transaction.commit()
            supportActionBar?.title = getString(R.string.app_bar_goals)
        } else {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.content_frame, ExploreFragment())
            transaction.commit()
            supportActionBar?.title = getString(R.string.app_bar_explore)
            mSnackbar.show()
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
        if (loggedInUserId != null) {
            sidebarNickname.setOnClickListener {
                val intent = Intent(this, UserActivity::class.java)

                startActivity(intent)
            }
            profileImage.setOnClickListener {
                val intent = Intent(this, UserActivity::class.java)
                intent.putExtra("user_id", loggedInUserId)
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
                    displayedFragment = MyGoalsFragment()
                    actionBarTitle = getString(R.string.app_bar_goals)
                }
                R.id.nav_library -> {
                    displayedFragment = ResourceLibraryFragment()
                    actionBarTitle = getString(R.string.app_bar_users_library)
                }
                R.id.nav_logout -> {
                    val userRepository = (application as GoalzApp).userRepository
                    userRepository.removeMessagingToken( FirebaseAuth.getInstance().currentUser!!.uid)
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, OnboardingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

            // Without login menu
                R.id.nav_login -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)

                }
                R.id.nav_signup -> {
                    val intent = Intent(this, SignupActivity::class.java)
                    startActivity(intent)
                }
            }
            item.isChecked = true

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

    //Method from IDialog Result Listener
    override fun callback(tag: String, result: IDialogResultListener.DialogResult, value: Any?) {
        val deleteDialog = getString(R.string.goal_activity_delete_dialog_tag)

        if (result == IDialogResultListener.DialogResult.CONFIRM) {
            if (tag == deleteDialog) {
                val goalRepository = (application as GoalzApp).goalRepository
                goalRepository.deleteGoal(value as String).observe(this,DeleteResponseObserver())
            }
        }
    }

    //Observers
    inner class DeleteResponseObserver : Observer<DalResponse> {
        override fun onChanged(response: DalResponse?) {
            if (response?.status == DalResponseStatus.SUCCESS) {
                Toast.makeText(application, application.getString(R.string.delete_goal_success_toast),
                        Toast.LENGTH_LONG).show()
            } else if (response?.status == DalResponseStatus.FAIL) {
                Toast.makeText(application, application.getString(R.string.delete_goal_fail_toast),
                        Toast.LENGTH_LONG).show()
            }
        }
    }
}