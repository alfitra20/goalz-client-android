package emse.mobisocial.goalz.ui

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.content.res.AppCompatResources
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.ui.viewModels.UserProfileViewModel
import emse.mobisocial.goalz.util.Gender
import kotlinx.android.synthetic.main.activity_create_goal.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    private lateinit var model : UserProfileViewModel
    private lateinit var mDateListener:DatePickerDialog.OnDateSetListener
    private lateinit var mSnackbar: Snackbar
    private var birthYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        model = ViewModelProviders.of(this).get(UserProfileViewModel::class.java)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: finish()

        model.getUser(userId.toString())
        initializeObservers()

        birthdate_edit_text.isEnabled = false
        pickDateListener()

        change_password_button.setOnClickListener {
            val intent = Intent(this, ChangeEmailPasswordActivity::class.java)
            intent.putExtra("user_id", userId.toString())
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_profile, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_save_edit_profile -> {
                updateUserData()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeObservers() {
        model.userData.observe(this, Observer<User> { user ->
            Log.d("check", "check")
            if (user != null) {
                setUserData(user)
                birthYear = user.age!!.toInt()
            }
        })
    }

    private fun setUserData(user:User){

        nickname_edit_text.setText(user.nickname)
        firstname_edit_text.setText(user.firstName)
        lastname_edit_text.setText(user.lastName)
        website_edit_text.setText(user.website)

        val spinnerArray = arrayOfNulls<String>(2)

        spinnerArray[0] = "FEMALE"
        spinnerArray[1] = "MALE"

        val adapter = ArrayAdapter<String>(this, R.layout.spinner_item, spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pick_gender.adapter = adapter

        if (user.gender.toString() == "MALE") {
            pick_gender.setSelection(1)
        } else{
            pick_gender.setSelection(0)
        }

    }

    private fun updateUserData() {
        var website = ""

        if (website_edit_text.text.toString() != ""){
            website = website_edit_text.text.toString()
        }

        val nickname = nickname_edit_text.text.toString()
        val firstname = firstname_edit_text.text.toString()
        val  lastname = lastname_edit_text.text.toString()
        var genderPosition = pick_gender.selectedItemPosition
        var gender :Gender
        if (genderPosition== 0){
            gender = Gender.valueOf("FEMALE")
        }else{
            gender  = Gender.valueOf("MALE")
        }
        if(birthYear < 12) {
            launchSnackbar("Need to be at least 12 years old")
        }else {
            model.modifyUserData(nickname, firstname, lastname, birthYear, website, gender)?.observe(this, Observer<DalResponse> { response ->
                if (response?.status == DalResponseStatus.SUCCESS) {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if (response?.status == DalResponseStatus.FAIL) {
                    launchSnackbar("Unable to Edit the Profile")
                }

            })
        }
    }

    private fun getAge(Year:Int):Int{
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR) - Year
    }

    private fun pickDateListener(){
        pickbirthdate_edit.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year:Int = calendar.get(Calendar.YEAR)
            val month:Int = calendar.get(Calendar.MONTH)
            val day:Int = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog (
                    this, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, mDateListener, year, month, day)
            dialog.datePicker.maxDate = System.currentTimeMillis()-1000
            dialog.show()
        }
        mDateListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year:Int, month:Int, day:Int ->
            birthYear = getAge(year)
            val datePicked = day.toString()+"/"+month.toString()+"/"+year.toString()
            birthdate_edit_text.setText(datePicked)
        }
    }

    private fun launchSnackbar(title: String) {
        mSnackbar = Snackbar.make(create_goal_layout, title, Snackbar.LENGTH_SHORT)
        mSnackbar.view.background = AppCompatResources.getDrawable(this, R.color.snackbarErrorColor)
        mSnackbar.show()
    }
}
