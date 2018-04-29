package emse.mobisocial.goalz.ui

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.drawable.RotateDrawable
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.ui.viewModels.CreateGoalViewModel
import emse.mobisocial.goalz.ui.viewModels.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_create_goal.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_user.*

import kotlin.collections.ArrayList
import kotlin.math.max


class UserActivity : AppCompatActivity() {
    private lateinit var model : UserProfileViewModel
    private var userId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        model = ViewModelProviders.of(this).get(UserProfileViewModel::class.java)

        userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {

            model.getUser(userId!!)
            model.getUsersGoal(userId!!)
            model.getUsersResource(userId!!)
            initializeObservers()
        }else{
            Log.e("CREATE A GOAL: ", "COULD NOT GET AUTHENTICATED USER")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initializeObservers() {
        model.userData.observe(this, Observer<User> { user ->
            Log.d("check", "check")
            if (user != null) {
                val fullName = user.firstName+" "+user.lastName
                full_name_text.text = fullName
                supportActionBar?.title = user.nickname
                age_text.text = user.age.toString()
                if(user.website == null){
                    website_text.text = "None"
                }else{
                    website_text.text = user.website
                }
                if(user.gender.toString() == ""){
                    gender_text.text = "None"
                }else{
                    gender_text.text = user.gender.toString()
                }
                val rating = user.rating
                user_point_text.text = rating.toString()

                rangkingProgress.progress = rating.toInt()
                when(rating){
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
        })
        model.usersGoal.observe(this, Observer<List<Goal>> { goals ->
            if(goals!=null){
                var completed = 0
                for (goal in goals){
                    if (goal.status == 100){
                        completed+=1
                    }
                }
                goal_count_text.text = goals.count().toString()
                goal_completed_count_text.text = completed.toString()
            }
        })
        model.usersResource.observe(this, Observer<List<Resource>>{ resources ->
            if(resources!=null){
                resource_count_text.text = resources.count().toString()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_user, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_edit_profile -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
