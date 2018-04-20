package emse.mobisocial.goalz.ui


import android.Manifest
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.GoalTemplate
import emse.mobisocial.goalz.ui.viewModels.FABGoalResourceVM
import kotlinx.android.synthetic.main.activity_create_goal.*
import java.text.SimpleDateFormat

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.db.converter.LocationConverter
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max


class CreateGoalActivity : AppCompatActivity() {
    private lateinit var model : FABGoalResourceVM
    // Temporary
    private val USER_ID = "FOlyCo0IILeOnfUxhZpphdYnICS2"

    private var currentLocation : Location = LocationConverter.toLocation("0.0,0.0")
    private lateinit var userGoalsList : ArrayList<Goal>
    private var spinnerMap : HashMap<Int, String> = HashMap()
    private lateinit var mDateListener:DatePickerDialog.OnDateSetListener
    private lateinit var client: FusedLocationProviderClient
    private lateinit var locationManager : LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_goal)
        supportActionBar?.title = "Create a new Goal"
        supportActionBar?.elevation = 0F

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        userGoalsList = ArrayList<Goal>()
        model = ViewModelProviders.of(this).get(FABGoalResourceVM::class.java)

        //using temporary user id
        model.setUser(USER_ID)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        client = LocationServices.getFusedLocationProviderClient(this)

        client.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                currentLocation.longitude = location.longitude
                currentLocation.latitude = location.latitude
            }
        }
        goalDeadlineText.isEnabled = false

        initializeObservers()
        initializeEventListeners()

    }
    private fun initializeObservers() {
        model.userGoalsList.observe(this, Observer<List<Goal>> { goals ->
            Log.d("check", "check")
            if (goals != null) {
                    val spinnerArray = arrayOfNulls<String>(max(goals.size,1))

                    spinnerMap[0] = "none"
                    spinnerArray[0] = "None"

                    for (i in 1 until goals.size)
                    {
                        spinnerMap[i] = goals[i].id.toString()
                        spinnerArray[i] = goals[i].title
                    }
                    val adapter = ArrayAdapter<String>(this, R.layout.spinner_item, spinnerArray)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    userGoalSpinner.adapter = adapter
                }
        })
    }
    private fun initializeEventListeners() {
        createGoalButton.setOnClickListener {createEventListener() }
        pickDate.post { pickDateListener() }
    }

    private fun pickDateListener(){
        pickDate.setOnClickListener {
            val calendar:Calendar = Calendar.getInstance()
            val year:Int = calendar.get(Calendar.YEAR)
            val month:Int = calendar.get(Calendar.MONTH)
            val day:Int = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog (
                    this, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, mDateListener, year, month, day)
            dialog.datePicker.minDate = System.currentTimeMillis()-1000
            dialog.show()
        }
        mDateListener = DatePickerDialog.OnDateSetListener { _:DatePicker, year:Int, month:Int, day:Int ->
            val datePicked = day.toString()+"/"+month.toString()+"/"+year.toString()
            goalDeadlineText.setText(datePicked)
        }
    }

    private fun  createEventListener(){
        var dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        goalDeadlineText

        var parentId = (spinnerMap[userGoalSpinner.selectedItemPosition])
        if (parentId == "none"){
            parentId = null
        }
        try {
            val date = dateFormat.parse(goalDeadlineText.text.toString())
            val newGoal = GoalTemplate(
                    USER_ID,
                    parentId,
                    goalTitleText.text.toString(),
                    goalTopicText.text.toString(),
                    goalDescriptionText.text.toString(),
                    currentLocation,
                    0,
                    date)
            addGoal(newGoal)
        }catch (e:Exception){
            Toast.makeText(this, "Invalid Fields", Toast.LENGTH_SHORT).show()
        }

    }

    private fun addGoal(newGoal:GoalTemplate){
        if(goalTitleText.text.toString()!=""&&goalTopicText.text.toString()!=""&&goalDescriptionText.text.toString()!=""&&goalDeadlineText.text.toString()!="") {

            model.addGoal(newGoal).observe(this, Observer<DalResponse> { id ->
                val intent = Intent(this, GoalActivity::class.java)
                intent.putExtra("new_goal_id", id.toString())
                startActivity(intent)
                Toast.makeText(this, "Goal Successfully Created", Toast.LENGTH_LONG).show()
                finish()
            })
        }else{
            Toast.makeText(this, "Invalid Fields", Toast.LENGTH_SHORT).show()
        }
    }
}
