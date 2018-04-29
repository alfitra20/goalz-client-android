package emse.mobisocial.goalz.ui

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.getAge
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import emse.mobisocial.goalz.GoalzApp

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.model.UserTemplate
import kotlinx.android.synthetic.main.activity_create_goal.*
import kotlinx.android.synthetic.main.fragment_signup_personal_info.*
import java.text.SimpleDateFormat
import java.util.*

private const val NICKNAME_PARAM = "nickname"
private const val EMAIL_PARAM = "email"
private const val PASSWORD_PARAM = "password"

class SignupPersonalInfoFragment : Fragment() {

    private lateinit var mDateListener:DatePickerDialog.OnDateSetListener
    private var nickname : String? = null
    private var email : String? = null
    private var password : String? = null
    private lateinit var first_name : String
    private lateinit var  last_name : String
    private lateinit var birthdate : String
    private lateinit var mContext : Context
    private lateinit var pickBirthdate : ImageButton
    private var age = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity.applicationContext
        arguments?.let {
            nickname = it.getString(NICKNAME_PARAM)
            email = it.getString(EMAIL_PARAM)
            password = it.getString(PASSWORD_PARAM)

        }
        Log.d("email", email)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup_personal_info, container, false)
        val firstnameText = view.findViewById<EditText>(R.id.firstname_text)
        val lastnameText  = view.findViewById<EditText>(R.id.lastname_text)
        val birthdateText = view.findViewById<EditText>(R.id.birthdate_text)
        val button = view.findViewById<Button>(R.id.register_button)
        pickBirthdate = view.findViewById(R.id.pickBirthDate) as ImageButton

        first_name = firstnameText.text.toString()
        last_name = lastnameText.text.toString()
        birthdate = birthdateText.text.toString()

        birthdateText.isEnabled = false
        pickDateListener()
        button.setOnClickListener { registerUser() }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun pickDateListener(){
        pickBirthdate.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year:Int = calendar.get(Calendar.YEAR)
            val month:Int = calendar.get(Calendar.MONTH)
            val day:Int = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog (
                    context, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, mDateListener, year, month, day)
            dialog.datePicker.maxDate = System.currentTimeMillis()-1000
            dialog.show()
        }
        mDateListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year:Int, month:Int, day:Int ->
            age = year
            val datePicked = day.toString()+"/"+month.toString()+"/"+year.toString()
            birthdate_text.setText(datePicked)
        }
    }

    private fun registerUser(){
        if(first_name == "" && last_name == "" && birthdate == ""){
            val calendar = Calendar.getInstance()
            val birthYear = calendar.get(Calendar.YEAR) - age
            if(birthYear < 12){
                Toast.makeText(mContext, "Need to be at least 12 years old to register", Toast.LENGTH_SHORT).show()
            }else {
               val newUser = UserTemplate(
                        nickname!!,
                        password!!,
                        first_name,
                        last_name,
                        email!!,
                        birthYear
                )
                val userRepository = (activity.application as GoalzApp).userRepository
                userRepository.registerUser(newUser).observe(this, Observer<DalResponse> { response ->
                    if (response?.status == DalResponseStatus.SUCCESS){
                        val intent = Intent(mContext, BaseActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        Toast.makeText(mContext, "Successfully Registered", Toast.LENGTH_LONG).show()
                    }else if (response?.status == DalResponseStatus.FAIL){
                        Toast.makeText(mContext, "Register unsuccessful, Try again", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }else if (first_name != ""){
            Toast.makeText(mContext, "First Name required", Toast.LENGTH_SHORT).show()
        }else if (last_name != ""){
            Toast.makeText(mContext, "Last Name required", Toast.LENGTH_SHORT).show()
        }else if (birthdate != ""){
            Toast.makeText(mContext, "Birth date required", Toast.LENGTH_SHORT).show()
        }
    }
}
