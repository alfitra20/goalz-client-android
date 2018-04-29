package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.ui.viewModels.EditGoalViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditGoalActivity : AppCompatActivity() {

    private lateinit var model: EditGoalViewModel
    private var user: FirebaseUser? = null

    private lateinit var titleEt: EditText
    private lateinit var topicEt: EditText
    private lateinit var descriptionEt: EditText
    private lateinit var deadlineEt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var goalId = intent.getStringExtra("goal_id") ?: finish()

        //Initialize model and user
        user = FirebaseAuth.getInstance().currentUser
        model = EditGoalViewModel(application, goalId as String)

        //Initialize views
        setContentView(R.layout.activity_edit_goal)

        titleEt = findViewById(R.id.edit_goal_activity_title_et)
        topicEt = findViewById(R.id.edit_goal_activity_topic_et)
        deadlineEt = findViewById(R.id.edit_goal_activity_deadline_et)
        descriptionEt = findViewById(R.id.edit_goal_activity_description_et)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);
        supportActionBar!!.setHomeButtonEnabled(true)


        //Set model observers
        model.goal.observe(this, GoalInfoObserver())
    }

    //Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_goal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.edit_goal_activity_done_menu_item -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    //Helper methods
    private fun stringToDate(dateText : String) : Date {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.parse(dateText)
    }

    private fun dateToString(date : Date?) : String {
        if (date == null) return ""
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    //Observers
    inner class GoalInfoObserver : Observer<Goal> {
        override fun onChanged(goal: Goal?) {
            if (goal == null) return

            titleEt.setText(goal.title, TextView.BufferType.EDITABLE)
            topicEt.setText(goal.topic, TextView.BufferType.EDITABLE)
            deadlineEt.setText(dateToString(goal.deadline), TextView.BufferType.EDITABLE)
            descriptionEt.setText(goal.description, TextView.BufferType.EDITABLE)
        }
    }

}
