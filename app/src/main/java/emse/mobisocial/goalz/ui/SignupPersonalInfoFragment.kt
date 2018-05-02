package emse.mobisocial.goalz.ui

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.content.res.AppCompatResources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.fragment_signup_personal_info.*
import java.util.*
import java.util.regex.Pattern

private const val NICKNAME_PARAM = "nickname"
private const val FIRSTNAME_PARAM = "firstname"
private const val LASTNAME_PARAM = "lastname"
private const val AGE_PARAM = "age"

class SignupPersonalInfoFragment : Fragment() {

    private lateinit var mDateListener:DatePickerDialog.OnDateSetListener
    private lateinit var firstName : String
    private lateinit var  lastName : String
    private lateinit var birthdate : String
    private lateinit var mContext : Context
    private lateinit var firstnameText :EditText
    private lateinit var lastnameText: EditText
    private lateinit var birthdateText: EditText
    private lateinit var pickBirthdate : ImageButton
    private lateinit var mSnackbar: Snackbar
    private var nickname : String? = null
    private var age : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity.applicationContext
        arguments?.let {
            nickname = it.getString(NICKNAME_PARAM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup_personal_info, container, false)
        firstnameText = view.findViewById(R.id.firstname_text)
        lastnameText  = view.findViewById(R.id.lastname_text)
        birthdateText = view.findViewById(R.id.birthdate_text)
        val button = view.findViewById<Button>(R.id.next_in_personal_info)
        pickBirthdate = view.findViewById(R.id.pickBirthDate) as ImageButton

        birthdateText.isEnabled = false
        birthdateText.isClickable = true
        pickBirthdate.setOnClickListener(PickDateOnClickListener())
        birthdateText.setOnClickListener(PickDateOnClickListener())
        button.setOnClickListener {
            // hiding the keyboard
            //val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            registerUser()
        }
        return view
    }

    inner class DateListener : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(datePicker : DatePicker, year : Int, month : Int, day : Int ) {
            age = year
            val datePicked = day.toString()+"/"+month.toString()+"/"+year.toString()
            birthdate_text.setText(datePicked)
        }
    }

    inner class PickDateOnClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val calendar:Calendar = Calendar.getInstance()
            val year:Int = calendar.get(Calendar.YEAR)
            val month:Int = calendar.get(Calendar.MONTH)
            val day:Int = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog (context, R.style.ThemeOverlay_AppCompat_Dialog,
                    DateListener(), year, month, day)
            dialog.datePicker.maxDate = System.currentTimeMillis()-1000
            dialog.show()
        }
    }

    private fun registerUser(){
        firstName = firstnameText.text.toString().trim()
        lastName = lastnameText.text.toString().trim()
        birthdate = birthdateText.text.toString().trim()

        if(firstName != "" && lastName != "" && birthdate != ""){
            val calendar = Calendar.getInstance()
            val birthYear = calendar.get(Calendar.YEAR) - age
            if(birthYear < 12){
                launchSnackbar(activity.application.getString(R.string.signup_activity_minimum_age))
            }else {
                var check = checkForSpecialCharacters(firstName, lastName)
                if (check) {
                    val args = Bundle()
                    args.putString(NICKNAME_PARAM, nickname)
                    args.putString(FIRSTNAME_PARAM, firstName)
                    args.putString(LASTNAME_PARAM, lastName)
                    args.putInt(AGE_PARAM, birthYear)
                    val newFragment = SignupCredentialsFragment()
                    newFragment.arguments = args
                    val transaction = activity.supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.signup_frame, newFragment).addToBackStack("tag")
                    transaction.commit()
                }
            }
        }else if (firstName == ""){
            launchSnackbar(activity.application.getString(R.string.signup_activity_firstname_required))
        }else if (lastName == ""){
            launchSnackbar(activity.application.getString(R.string.signup_activity_lastname_required))
        }else if (birthdate == ""){
            launchSnackbar(activity.application.getString(R.string.signup_activity_birthdate_required))
        }
    }

    private fun checkForSpecialCharacters(firstname:String, lastname:String):Boolean{
        val pattern = Pattern.compile("[a-zA-Z.? ]*")
        val firstnameCheck = pattern.matcher(firstname)
        val lastnameCheck = pattern.matcher(lastname)
        var check = true
        if (!firstnameCheck.matches() && !lastnameCheck.matches()) {
            launchSnackbar(activity.application.getString(R.string.signup_activity_invalid_fields_snackbar))
            check = false
        }
        return check
    }

    private fun launchSnackbar(title: String) {
        mSnackbar = Snackbar.make(signup_personal_info_layout, title, Snackbar.LENGTH_LONG)
        mSnackbar.view.background = AppCompatResources.getDrawable(mContext, R.color.snackbarErrorColor)
        mSnackbar.show()
    }

}
