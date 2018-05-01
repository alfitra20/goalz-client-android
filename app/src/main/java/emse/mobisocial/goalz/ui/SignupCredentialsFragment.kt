package emse.mobisocial.goalz.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.fragment_signup_credentials.*
import android.content.Intent
import android.support.v7.content.res.AppCompatResources.getDrawable
import android.widget.Toast
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.model.UserTemplate
import emse.mobisocial.goalz.ui.viewModels.UserProfileViewModel


private const val NICKNAME_PARAM = "nickname"
private const val FIRSTNAME_PARAM = "firstname"
private const val LASTNAME_PARAM = "lastname"
private const val AGE_PARAM = "age"

class SignupCredentialsFragment : Fragment() {

    private lateinit var model : UserProfileViewModel
    private var nickname : String? = null
    private var firstname : String? = null
    private var lastname : String? = null
    private var age : Int? = null
    private lateinit var mContext :Context
    private lateinit var mSnackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity.applicationContext
        model = ViewModelProviders.of(this).get(UserProfileViewModel::class.java)
        arguments.let{
            nickname = it.getString(NICKNAME_PARAM)
            firstname = it.getString(FIRSTNAME_PARAM)
            lastname = it.getString(LASTNAME_PARAM)
            age = it.getInt(AGE_PARAM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_signup_credentials, container, false)
        val emailText = view.findViewById<EditText>(R.id.email_text)
        val passwordText = view.findViewById<EditText>(R.id.password_text)
        val confirmPasswordText = view.findViewById<EditText>(R.id.confirm_password_text)
        val button = view.findViewById<Button>(R.id.register_user_button)

        button.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()
            val confirmPassword = confirmPasswordText.text.toString().trim()
            val checkEmail = isValidEmail(email)
            if (email != "" && password != "" && confirmPassword != "") {
                if (checkEmail) {
                    if (password != confirmPassword) {
                        launchSnackbar("Confirm password must be the same")
                    } else if (password.length < 8){
                        launchSnackbar("Password is too short, Min: 8 Characters")
                    } else {

                        val newUser = UserTemplate(
                                nickname!!,
                                password,
                                firstname!!,
                                lastname!!,
                                email,
                                age
                        )
                        registerUser(newUser)
                    }
                } else {
                    launchSnackbar("Wrong Email Format")
                }
            }else if (email == "" && password == "" && confirmPassword == ""){
                launchSnackbar("Please fill in all the Fields")
            }else if (email == ""){
                launchSnackbar("Email is required")
            }else if (password == ""){
                launchSnackbar("Password is required")
            }else if (confirmPassword == ""){
                launchSnackbar("Confirm Password required")
            }
        }
        return view
    }

    fun isValidEmail(target: String): Boolean {
        return if (target == "") false else android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()

    }
    private fun launchSnackbar(title: String) {
        mSnackbar = Snackbar.make(signup_credentials_layout, title, Snackbar.LENGTH_LONG)
        mSnackbar.view.background = getDrawable(mContext, R.color.snackbarErrorColor)
        mSnackbar.show()
    }

    @SuppressLint("ResourceAsColor")
    private fun registerUser(newUser: UserTemplate) {
        model.registerNewUser(newUser).observe(this, Observer<DalResponse> { response ->
            if (response?.status == DalResponseStatus.SUCCESS) {
                val intent = Intent(mContext, BaseActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(mContext, "Successfully Registered", Toast.LENGTH_LONG).show()
            } else if (response?.status == DalResponseStatus.FAIL) {
                val loginIntent = Intent(mContext, LoginActivity::class.java)
                mSnackbar = Snackbar.make(signup_credentials_layout, "Email already Registered", Snackbar.LENGTH_LONG)
                mSnackbar.view.background = getDrawable(mContext, R.color.snackbarErrorColor)
                mSnackbar.setAction("Login") { startActivity(loginIntent) }
                mSnackbar.setActionTextColor(R.color.snackbarActionColor)
                mSnackbar.show()
            }
        })
    }
}
