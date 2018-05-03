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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.ui.viewModels.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {
    private lateinit var model : UserProfileViewModel
    private var isOwnProfile = false

    private lateinit var nicknameTw : TextView
    private lateinit var fullNameTw : TextView
    private lateinit var ageTw : TextView
    private lateinit var genderTw : TextView
    private lateinit var websiteTw : TextView
    private lateinit var levelTw : TextView
    private lateinit var pointsToLevelTw : TextView
    private lateinit var rankingPb : ProgressBar
    private lateinit var rankingIw : ImageView
    private lateinit var rankingTw : TextView
    private lateinit var goalsLabelTw : TextView
    private lateinit var goalsTw : TextView
    private lateinit var resourcesTw : TextView

    private lateinit var rankingImgArray : Array<Int>
    private lateinit var rankingLevelsMax : Array<Int>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("user_id") ?: finish()
        val authUserId = FirebaseAuth.getInstance().currentUser?.uid

        isOwnProfile = (authUserId != null && userId == authUserId)

        model = ViewModelProviders.of(this).get(UserProfileViewModel::class.java)
        model.initialize(userId as String)

        rankingImgArray = arrayOf(R.drawable.level_1, R.drawable.level_2, R.drawable.level_3, R.drawable.level_4)
        rankingLevelsMax = arrayOf(10, 50, 200)

        setContentView(R.layout.activity_user)

        nicknameTw = findViewById(R.id.user_activity_nickname_tw)
        fullNameTw = findViewById(R.id.user_activity_full_name_tw)
        ageTw = findViewById(R.id.user_activity_age_tw)
        genderTw = findViewById(R.id.user_activity_gender_tw)
        websiteTw = findViewById(R.id.user_activity_website_tw)
        levelTw = findViewById(R.id.user_activity_level_tw)
        pointsToLevelTw = findViewById(R.id.user_activity_level_remaining_points_tw)
        rankingPb = findViewById(R.id.user_activity_ranking_pb)
        rankingIw = findViewById(R.id.user_activity_ranking_iw)
        rankingTw = findViewById(R.id.user_activity_ranking_tw)
        goalsLabelTw = findViewById(R.id.user_activity_coal_completed_tw)
        goalsTw = findViewById(R.id.user_activity_goal_completed_no_tw)
        resourcesTw = findViewById(R.id.user_activity_resource_no_tw)

        initializeObservers()

        goalsTw.setOnClickListener { launchIntent(0) }
        goalsLabelTw.setOnClickListener { launchIntent(0) }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initializeObservers() {
        model.userData.observe(this, Observer<User> { user ->
            if (user != null) { setUserData(user) }
        })
        model.usersGoal.observe(this, Observer<List<Goal>> { goals ->
            if(goals!=null){ setCompletedGoal(goals) }
        })
        model.usersResource.observe(this, Observer<List<Resource>>{ resources ->
            if(resources!=null){ resourcesTw.text = resources.count().toString() }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(isOwnProfile) {
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
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchIntent(position:Int){
        if (position == 0 && !isOwnProfile){
            val user = model.userData.value ?: return

            val intent = Intent(this, GoalsActivity::class.java)
            intent.putExtra("user_id", user.id)
            intent.putExtra("user_nickname", user.nickname)
            startActivity(intent)
        }
        else {
            val intent = Intent(this, BaseActivity::class.java)
            intent.putExtra("position", position)
            startActivity(intent)
        }

    }

    private fun setUserData(user:User){
        supportActionBar?.title = "${user.nickname}${getString(R.string.user_activity_title_template)}"

        nicknameTw.text = user.nickname
        fullNameTw.text = "${user.firstName} ${user.lastName}"
        ageTw.text = user.age.toString()
        websiteTw.text = if (user.website == null) "none" else user.website
        genderTw.text = user.gender.toString()
        rankingTw.text = user.rating.toString()

        var level = when(user.rating){
            in 0..10 -> { 1 }
            in 11..50 -> { 2 }
            in 51..200 -> { 3 }
            else -> { 4 }
        }
        levelTw.text = level.toString()

        rankingIw.setImageResource(rankingImgArray[level-1])
        pointsToLevelTw.text = if (level == 4) "-" else (rankingLevelsMax[level-1] - user.rating).toString()
        if(level != 4) {
            rankingPb.progress = user.rating.toInt()
            rankingPb.max = rankingLevelsMax[level-1]
        }
        else {
            rankingPb.progress = 100
            rankingPb.max = 100
        }

    }

    private fun setCompletedGoal(goals:List<Goal>){
        var completed = 0
        goals.filter { it.status == 100 }.forEach { completed+=1 }
        goalsTw.text = "$completed/${goals.count()}"
    }

}
