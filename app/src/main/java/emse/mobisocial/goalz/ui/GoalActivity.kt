package emse.mobisocial.goalz.ui

import android.os.Bundle

import emse.mobisocial.goalz.R
import android.view.*
import android.widget.TextView
import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v4.app.*
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.ui.viewModels.GoalViewModel
import java.util.*
import android.view.MenuInflater



class GoalActivity : AppCompatActivity() {

    private lateinit var model : GoalViewModel
    private var user : FirebaseUser? = null

    private lateinit var subgoalFragment: GoalActivitySubgoalsFragment
    private lateinit var recommendationFragment: GoalActivitySubgoalsFragment
    private lateinit var goalInfoVp : ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var tabToFragmentMap : HashMap<Int, Fragment>

    private lateinit var descriptionTw : TextView
    private lateinit var statusPb : ProgressBar
    private lateinit var statusTw : TextView
    private lateinit var topicTw : TextView
    private lateinit var deadlineTw : TextView
    private lateinit var titleTw : TextView

    private lateinit var toolbar : Toolbar
    private var menuToChoose : Int = R.menu.menu_goal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var goalId = intent.getStringExtra("goal_id")
        if(goalId == null){
            finish()
        }

        //Initialize model and user
        user = FirebaseAuth.getInstance().currentUser
        model = GoalViewModel(application, goalId)

        //Initialize model and subFragments
        subgoalFragment = GoalActivitySubgoalsFragment()
        recommendationFragment = GoalActivitySubgoalsFragment()
        tabToFragmentMap = hashMapOf(1 to subgoalFragment, 2 to recommendationFragment)

        //Initialize views
        setContentView(R.layout.activity_goal)

        toolbar = findViewById(R.id.goal_activity_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);

        descriptionTw = findViewById(R.id.goal_activity_description_tw)
        statusPb = findViewById(R.id.goal_activity_status_pb)
        statusTw = findViewById(R.id.goal_activity_status_tw)
        topicTw = findViewById(R.id.goal_activity_topic_tw)
        deadlineTw = findViewById(R.id.goal_activity_deadline_tw)
        titleTw = findViewById(R.id.goal_activity_title_tw)

        tabLayout = findViewById(R.id.goal_activity_tab_layout)
        goalInfoVp = findViewById(R.id.goal_activity_info_vp)

        goalInfoVp.adapter = GoalInfoPagerAdapter(supportFragmentManager)
        goalInfoVp.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabChangeListener())

        initializeObservers()
    }

    private fun initializeObservers() {
        model.state.observe(this, Observer<GoalViewModel.State> {state ->
            Log.d("GOAL", "current state" + state)
            when (state) {
                GoalViewModel.State.UNAUTH -> menuToChoose = R.menu.menu_goal
                GoalViewModel.State.AUTH_UNAUTHORIZED -> menuToChoose = R.menu.menu_goal_auth
                GoalViewModel.State.AUTH_AUTHORIZE -> menuToChoose = R.menu.menu_goal_autorized
            }

            invalidateOptionsMenu()
        })
        model.goal.observe(this, Observer<Goal> {goal ->
            if (goal != null) {

                titleTw.text = goal.title
                topicTw.text = goal.topic
                statusTw.text = goal.status.toString() + getString(R.string.goal_activity_status_template)
                statusPb.progress = goal.status
                deadlineTw.text = buildDeadlineText(goal.deadline)
                descriptionTw.text = goal.description

                val loggedInUserId = user?.uid
                if (loggedInUserId != null){
                    val userId = goal.userId
                    if (userId == loggedInUserId){
                        model.changeState(GoalViewModel.State.AUTH_AUTHORIZE)
                    }
                    else {
                        model.changeState(GoalViewModel.State.AUTH_UNAUTHORIZED)
                    }
                }
            }
        })
        model.subgoals.observe(this, Observer<List<Goal>> {goals ->
            if (goals != null) {
                subgoalFragment.updateContent(goals)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(menuToChoose, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id) {
            R.id.goal_activity_edit_menu_item -> {
                val intent = Intent(this, EditGoalActivity::class.java)
                intent.putExtra("goal_id", model.goalId)
                return true
            }
            R.id.goal_activity_recommend_menu_item -> {
                //TODO: Craete activity for recommendation creation and redirect there
                //val intent = Intent(this, CreateResourceActivity::class.java)
                //intent.putExtra("goal_id", model.goalId)
                return true
            }
            R.id.goal_activity_edit_menu_item -> {
                //TODO: Popup dialog for remvoe
                //val intent = Intent(this, CreateResourceActivity::class.java)
                //intent.putExtra("goal_id", model.goalId)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun buildDeadlineText(deadline : Date?) : String{
        if(deadline != null){
            val diffDays = (Date().getTime() - deadline.getTime()) / (24 * 60 * 60 * 1000)
            if(diffDays == 1L){
                return diffDays.toString() + getString(R.string.goal_activity_deadline_day_template)
            }
            else {
                return diffDays.toString() + getString(R.string.goal_activity_deadline_days_template)
            }
        }

        return getString(R.string.goal_activity_deadline_no_set)
    }

    inner class TabChangeListener : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            goalInfoVp.currentItem = tab.position
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            return
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            return
        }
    }

    inner class GoalInfoPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(i: Int): Fragment {
            when(i){
                0 -> return subgoalFragment
                1 -> return recommendationFragment
            }

            //This code should never be reached
            return subgoalFragment
        }

        override fun getCount(): Int {
            return 2
        }
    }
}

