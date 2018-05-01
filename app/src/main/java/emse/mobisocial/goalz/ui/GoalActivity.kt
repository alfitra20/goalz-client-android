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
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.ui.viewModels.GoalViewModel
import java.util.*
import android.widget.Toast
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.util.IDialogResultListener

class GoalActivity : AppCompatActivity(), IDialogResultListener {

    private lateinit var model: GoalViewModel
    private var user: FirebaseUser? = null

    private lateinit var subgoalFragment: GoalActivitySubgoalsFragment
    private lateinit var recommendationFragment: GoalActivitySubgoalsFragment
    private lateinit var goalInfoVp: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var tabToFragmentMap: HashMap<Int, Fragment>

    private lateinit var descriptionTw: TextView
    private lateinit var statusPb: ProgressBar
    private lateinit var statusTw: TextView
    private lateinit var topicTw: TextView
    private lateinit var deadlineTw: TextView
    private lateinit var titleTw: TextView

    private lateinit var toolbar: Toolbar
    private var menuToChoose: Int = R.menu.menu_goal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var goalId = intent.getStringExtra("goal_id") ?: finish()

        //Initialize model and user
        user = FirebaseAuth.getInstance().currentUser
        model = GoalViewModel(application, goalId as String)

        //Initialize subFragments
        subgoalFragment = GoalActivitySubgoalsFragment()
        recommendationFragment = GoalActivitySubgoalsFragment()
        tabToFragmentMap = hashMapOf(1 to subgoalFragment, 2 to recommendationFragment)

        //Initialize views
        setContentView(R.layout.activity_goal)

        toolbar = findViewById(R.id.goal_activity_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener { onBackPressed() }

        descriptionTw = findViewById(R.id.goal_activity_description_tw)
        statusPb = findViewById(R.id.goal_activity_status_pb)
        statusTw = findViewById(R.id.goal_activity_status_tw)
        topicTw = findViewById(R.id.goal_activity_topic_tw)
        deadlineTw = findViewById(R.id.goal_activity_deadline_tw)
        titleTw = findViewById(R.id.goal_activity_title_tw)
        tabLayout = findViewById(R.id.goal_activity_tab_layout)
        goalInfoVp = findViewById(R.id.goal_activity_info_vp)

        //Initialize subfragments and tab adapter
        goalInfoVp.adapter = GoalInfoPagerAdapter(supportFragmentManager)
        goalInfoVp.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabChangeListener())

        //Set model observers
        model.state.observe(this, GoalStateObserver())
        model.goal.observe(this, GoalInfoObserver())
        model.subgoals.observe(this, SubgoalsListObserver())

        //Set action observers
        statusPb.setOnClickListener (StatusBarOnClickListener())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(menuToChoose, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.goal_activity_edit_menu_item -> {
                val intent = Intent(this, EditGoalActivity::class.java)
                intent.putExtra("goal_id", model.goalId)
                startActivity(intent)
                return true
            }
            R.id.goal_activity_recommend_menu_item -> {
                val intent = Intent(this, CreateRecommendationActivity::class.java)
                intent.putExtra("goal_id", model.goalId)
                startActivity(intent)
                return true
            }
            R.id.goal_activity_subgoal_menu_item -> {
                val intent = Intent(this, CreateGoalActivity::class.java)
                intent.putExtra("parentId", model.goalId)
                startActivity(intent)
                return true
            }
            R.id.goal_activity_delete_menu_item -> {
                val dialogFragment = GoalActivityDeleteDialog()
                dialogFragment.show(fragmentManager, getString(R.string.goal_activity_delete_dialog_tag))
                return true
            }
            R.id.goal_activity_clone_menu_item -> {
                val intent = Intent(this, CreateGoalActivity::class.java)
                intent.putExtra("title", titleTw.text)
                intent.putExtra("topic", topicTw.text)
                intent.putExtra("description", descriptionTw.text)
                startActivity(intent)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    //Method from IDialog Result Listener
    override fun callback(tag: String, result: IDialogResultListener.DialogResult, value: Any?) {
        val progressDialog = getString(R.string.goal_activity_progress_dialog_tag)
        val deleteDialog = getString(R.string.goal_activity_delete_dialog_tag)

        if (result == IDialogResultListener.DialogResult.CONFIRM) {
            when (tag) {
                progressDialog -> {
                    val newStatus = value as Int
                    model.updateProgress(newStatus)?.observe(this, UpdateResponseObserver())
                }
                deleteDialog -> {
                    model.deleteGoal().observe(this, DeleteResponseObserver())
                }
            }
        }

    }

    //Text setup method
    private fun buildDeadlineText(deadline: Date?): String {
        if (deadline != null) {
            val diffDays = (deadline.time - Date().time) / (24 * 60 * 60 * 1000)
            return if (diffDays == 1L) {
                diffDays.toString() + getString(R.string.goal_activity_deadline_day_template)
            } else {
                diffDays.toString() + getString(R.string.goal_activity_deadline_days_template)
            }
        }

        return getString(R.string.goal_activity_deadline_no_set)
    }


    //Control listener
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

    inner class StatusBarOnClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val dialogFragment = GoalActivityProgressDialog()
            val args = Bundle()

            args.putInt("progress_value", statusPb.progress)
            dialogFragment.arguments = args

            dialogFragment.show(fragmentManager, getString(R.string.goal_activity_progress_dialog_tag))
        }
    }

    //Fragment adapter
    inner class GoalInfoPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(i: Int): Fragment {
            when (i) {
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

    //Observers
    inner class DeleteResponseObserver : Observer<DalResponse> {
        override fun onChanged(response: DalResponse?) {
            if (response?.status == DalResponseStatus.SUCCESS) {
                Toast.makeText(application, application.getString(R.string.goal_activity_delete_goal_success_toast),
                        Toast.LENGTH_LONG).show()
                finish()
            } else if (response?.status == DalResponseStatus.FAIL) {
                Toast.makeText(application, application.getString(R.string.goal_activity_delete_goal_fail_toast),
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    inner class UpdateResponseObserver() : Observer<DalResponse> {
        override fun onChanged(response: DalResponse?) {
            if (response?.status == DalResponseStatus.SUCCESS) {
                Toast.makeText(application, application.getString(R.string.goal_activity_update_status_success_toast),
                        Toast.LENGTH_LONG).show()
            } else if (response?.status == DalResponseStatus.FAIL) {
                Toast.makeText(application, application.getString(R.string.goal_activity_update_status_fail_toast),
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    inner class GoalStateObserver : Observer<GoalViewModel.State> {
        override fun onChanged(state: GoalViewModel.State?) {
            when (state) {
                GoalViewModel.State.UNAUTH -> menuToChoose = R.menu.menu_goal
                GoalViewModel.State.AUTH_UNAUTHORIZED -> menuToChoose = R.menu.menu_goal_auth
                GoalViewModel.State.AUTH_AUTHORIZE -> menuToChoose = R.menu.menu_goal_autorized
            }

            invalidateOptionsMenu()
        }
    }

    inner class GoalInfoObserver : Observer<Goal> {
        override fun onChanged(goal: Goal?) {
            if (goal == null) return

            titleTw.text = goal.title
            topicTw.text = goal.topic
            statusTw.text = goal.status.toString() + getString(R.string.goal_activity_status_template)
            statusPb.progress = goal.status
            deadlineTw.text = buildDeadlineText(goal.deadline)
            descriptionTw.text = goal.description

            val loggedInUserId = user?.uid
            if (loggedInUserId != null) {
                val userId = goal.userId
                if (userId == loggedInUserId) {
                    model.changeState(GoalViewModel.State.AUTH_AUTHORIZE)
                } else {
                    model.changeState(GoalViewModel.State.AUTH_UNAUTHORIZED)
                }
            }
        }
    }

    inner class SubgoalsListObserver : Observer<List<Goal>> {
        override fun onChanged(goals: List<Goal>?) {
            if (goals != null) {
                subgoalFragment.updateContent(goals)
            }
        }
    }
}

