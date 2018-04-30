package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.ui.viewModels.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {
    private lateinit var model : UserProfileViewModel
    private var userId:String? = null
    private var loggedin = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        model = ViewModelProviders.of(this).get(UserProfileViewModel::class.java)
        val userIdFromExplore : String? = intent.getStringExtra("user_id")
        userId = FirebaseAuth.getInstance().currentUser?.uid


        if(userIdFromExplore != null){
            initializeModel(userIdFromExplore)
        }else{
            if(userId!=null) {
                initializeModel(userId!!)
                loggedin = true
            }
        }
        initializeOnCLickListener()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initializeObservers() {
        model.userData.observe(this, Observer<User> { user ->
            Log.d("check", "check")
            if (user != null) {
                setUserData(user)
            }
        })
        model.usersGoal.observe(this, Observer<List<Goal>> { goals ->
            if(goals!=null){
                setCompletedGoal(goals)
            }
        })
        model.usersResource.observe(this, Observer<List<Resource>>{ resources ->
            if(resources!=null){
                resource_count_text.text = resources.count().toString()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(loggedin) {
            menuInflater.inflate(R.menu.menu_user, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_profile -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initializeModel(userid : String){
        model.getUser(userid)
        model.getUsersGoal(userid)
        model.getUsersResource(userid)
        initializeObservers()
    }

    private fun initializeOnCLickListener(){
        goal_completed_text_button.setOnClickListener { launchIntent(0) }
        goal_created_text_button.setOnClickListener { launchIntent(0) }
        resource_text_button.setOnClickListener { launchIntent(2) }
        goal_completed_count_text.setOnClickListener { launchIntent(0) }
        goal_count_text.setOnClickListener { launchIntent(0) }
        resource_count_text.setOnClickListener{ launchIntent(2) }
    }

    private fun launchIntent(position:Int){
        val intent = Intent(this, BaseActivity::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    private fun setUserData(user:User){
        val fullName = user.firstName+" "+user.lastName
        full_name_text.text = fullName
        supportActionBar?.title = user.nickname
        age_text.text = user.age.toString()
        website_text.text = checkNull(user.website)
        gender_text.text = checkNull(user.gender.toString())
        user_point_text.text = user.rating.toString()
        rangkingProgress.progress = user.rating.toInt()

        when(user.rating){
            in 0..10 -> {
                image_ranking.setImageResource(R.drawable.level_1)
                point_needed_text.text = (10 - user.rating).toString()
                rangkingProgress.max = 10
            }
            in 11..50 -> {
                image_ranking.setImageResource(R.drawable.level_2)
                point_needed_text.text = (50 - user.rating).toString()
                rangkingProgress.max = 50
            }
            in 51..200 -> {
                image_ranking.setImageResource(R.drawable.level_3)
                point_needed_text.text = (200 - user.rating).toString()
                rangkingProgress.max = 200
            }
            else -> {
                image_ranking.setImageResource(R.drawable.level_4)
                point_needed_text.text = ""
                point_needed_description_text.text=""
                rangkingProgress.max = 100
                rangkingProgress.progress = 100
            }
        }
    }

    private fun checkNull(string : String?) : String{
        var result = if (string == null) "None" else string
        return result
    }
    private fun setCompletedGoal(goals:List<Goal>){
        var completed = 0
        for (goal in goals){
            if (goal.status == 100){
                completed+=1
            }
        }
        goal_count_text.text = goals.count().toString()
        goal_completed_count_text.text = completed.toString()
    }

}
