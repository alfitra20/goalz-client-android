package emse.mobisocial.goalz.ui

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
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
    private lateinit var datePicker:ImageButton

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
        datePicker = findViewById(R.id.edit_goal_activity_pick_date_ib)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);
        supportActionBar!!.setHomeButtonEnabled(true)


        //Set model observers
        model.goal.observe(this, GoalInfoObserver())

        //Set action listeners
        datePicker.setOnClickListener(PickDateOnClickListener())
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
                val title = titleEt.text.toString()
                val topic = topicEt.text.toString()
                val description = descriptionEt.text.toString()
                val deadline = if (deadlineEt.text.toString() == "") null else stringToDate(deadlineEt.text.toString())

                model.updateGoal(title, topic, description, deadline)?.observe(this, UpdateResponseObserver())
                return true
            }
            android.R.id.home -> {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item)
    }

    //Helper methods
    private fun stringToDate(dateText : String?) : Date {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.parse(dateText)
    }

    private fun dateToString(date : Date?) : String {
        if (date == null) return ""
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    // Control listeners
    inner class DateListener : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(datePicker : DatePicker, year : Int, month : Int, day : Int ) {
            val dateStr = day.toString() + "/" + (month + 1).toString() + "/" + year.toString()
            deadlineEt.setText(dateStr)
        }

    }

    inner class PickDateOnClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val calendar:Calendar = Calendar.getInstance()
            val year:Int = calendar.get(Calendar.YEAR)
            val month:Int = calendar.get(Calendar.MONTH)
            val day:Int = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog (this@EditGoalActivity, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,
                    DateListener(), year, month, day)
            dialog.datePicker.minDate = System.currentTimeMillis()-1000
            dialog.show()
        }

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

    inner class UpdateResponseObserver : Observer<DalResponse> {
        override fun onChanged(response: DalResponse?) {
            if (response?.status == DalResponseStatus.SUCCESS) {
                Toast.makeText(application, application.getString(R.string.edit_goal_activity_update_goal_success_toast),
                        Toast.LENGTH_LONG).show()
                this@EditGoalActivity.finish()
            } else if (response?.status == DalResponseStatus.FAIL) {
                Toast.makeText(application, application.getString(R.string.edit_goal_activity_update_goal_fail_toast),
                        Toast.LENGTH_LONG).show()
            }
        }
    }

}
