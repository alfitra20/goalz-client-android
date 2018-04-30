package emse.mobisocial.goalz.ui

import android.Manifest
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.GoalTemplate
import emse.mobisocial.goalz.ui.viewModels.CreateGoalViewModel
import kotlinx.android.synthetic.main.activity_create_goal.*
import java.text.SimpleDateFormat

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.dal.db.converter.LocationConverter
import kotlinx.android.synthetic.main.fragment_signup_credentials.*
import java.util.*
import kotlin.collections.HashMap

class CreateGoalActivity : AppCompatActivity() {

    private lateinit var model : CreateGoalViewModel

    private var spinnerMap : HashMap<Int, String> = HashMap()

    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationManager : LocationManager
    private var currentLocation : Location = LocationConverter.toLocation("0.0,0.0")

    private lateinit var titleEt: EditText
    private lateinit var topicEt: EditText
    private lateinit var descriptionEt: EditText
    private lateinit var deadlineEt: EditText
    private lateinit var parentSpinner: Spinner
    private lateinit var submitBtn : Button
    private lateinit var pickDateIb : ImageButton
    private lateinit var mSnackbar: Snackbar
    private var redColor = Color.parseColor("#FF6347")

    private var selectedParentId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get firebase user or close the app on fail
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: finish()

        //Request location permision in case we don't have
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

        //Try to get parameters from intent in case we come from clone or add subgoal
        val title = intent.getStringExtra("title")
        val topic = intent.getStringExtra("topic")
        val description = intent.getStringExtra("description")
        selectedParentId = intent.getStringExtra("parentId")

        //Initialize model
        model = ViewModelProviders.of(this).get(CreateGoalViewModel::class.java)
        model.setUser(userId as String)

        //Initialize location
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        locationProvider.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                currentLocation.longitude = location.longitude
                currentLocation.latitude = location.latitude
            }
        }

        //Start initializing the view
        setContentView(R.layout.activity_create_goal)
        supportActionBar?.title = getString(R.string.create_goal_activity_appbar_title)
        supportActionBar?.elevation = 0F

        titleEt = findViewById(R.id.create_goal_activity_title_et)
        topicEt = findViewById(R.id.create_goal_activity_description_et)
        descriptionEt = findViewById(R.id.create_goal_activity_topic_et)
        deadlineEt = findViewById(R.id.create_goal_activity_deadline_et)
        parentSpinner = findViewById(R.id.create_goal_activity_parent_sp)
        submitBtn = findViewById(R.id.create_goal_activity_submit_btn)
        pickDateIb = findViewById(R.id.create_goal_activity_pick_date_ib)

        //Set initial values in case we come from a clone action
        if (title != null) titleEt.setText(title)
        if (topic != null) topicEt.setText(topic)
        if (description != null) descriptionEt.setText(description)

        //Initialize observers
        model.userGoalsList.observe(this, UserGoalsObserver())
        pickDateIb.setOnClickListener(PickDateOnClickListener())
        submitBtn.setOnClickListener(CreateBtnOnClickListener())

    }

    private fun addGoal(newGoal:GoalTemplate){
        model.addGoal(newGoal).observe(this, CreateGoalResponseObserver())
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

            val dialog = DatePickerDialog (this@CreateGoalActivity, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,
                    DateListener(), year, month, day)
            dialog.datePicker.minDate = System.currentTimeMillis()-1000
            dialog.show()
        }

    }

    inner class CreateBtnOnClickListener : View.OnClickListener {
        override fun onClick(p0: View?) {
            var dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            var parentId = (spinnerMap[parentSpinner.selectedItemPosition])
            if (parentId == "none"){ parentId = null }
            val title = titleEt.text.toString()
            val description = descriptionEt.text.toString()
            val topic = topicEt.text.toString()
            var date : Date? = null
            if(!areValidFields(title, topic, description)) {
                launchSnackbar(getString(R.string.create_goal_activity_invalid_fields_toast))
                return
            }

            try {
                date = dateFormat.parse(deadlineEt.text.toString())
            }catch (e:Exception){
                //This block should be empty because if no date is given we proceed with null date
            }

            //Create new goal template
            val newGoal = GoalTemplate(model.userId!!, parentId, title,description, topic, currentLocation, 0, date)
            //Call add method from the parent
            addGoal(newGoal)
        }

        private fun areValidFields(title : String, topic : String, description: String) : Boolean{
            return title != "" && description != "" && topic != ""
        }

        private fun launchSnackbar(title: String) {
            mSnackbar = Snackbar.make(create_goal_layout, title, Snackbar.LENGTH_SHORT)
            mSnackbar.view.background = ColorDrawable(redColor)
            mSnackbar.show()
        }
    }

    // Observers
    inner class UserGoalsObserver : Observer<List<Goal>> {
        override fun onChanged(goals: List<Goal>?) {
            if (goals == null) return

            val spinnerArray = arrayOfNulls<String>(goals.size + 1)
            var selectedPosition = 0

            spinnerMap[0] = "none"
            spinnerArray[0] = "None"

            var positionIterator = 1
            for (goal in goals) {
                spinnerMap[positionIterator] = goal.id
                spinnerArray[positionIterator] = goal.title

                if (selectedParentId == goal.id) {
                    selectedPosition = positionIterator
                }
                positionIterator++
            }

            val adapter = ArrayAdapter<String>(this@CreateGoalActivity, R.layout.spinner_item, spinnerArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            parentSpinner.adapter = adapter

            parentSpinner.setSelection(selectedPosition)
        }
    }

    inner class CreateGoalResponseObserver : Observer<DalResponse> {
        override fun onChanged(response: DalResponse?) {
            if (response?.status == DalResponseStatus.SUCCESS){
                val intent = Intent(this@CreateGoalActivity, GoalActivity::class.java)
                intent.putExtra("goal_id", response.id)
                startActivity(intent)

                Toast.makeText(this@CreateGoalActivity,
                        getString(R.string.create_goal_activity_success_toast),
                        Toast.LENGTH_LONG).show()
                finish()
            }
        }

    }


}
