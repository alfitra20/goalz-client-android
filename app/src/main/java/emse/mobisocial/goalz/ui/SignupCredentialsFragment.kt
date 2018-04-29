package emse.mobisocial.goalz.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import emse.mobisocial.goalz.R

private const val NICKNAME_PARAM = "nickname"
private const val EMAIL_PARAM = "email"
private const val PASSWORD_PARAM = "password"

class SignupCredentialsFragment : Fragment() {

    private var nickname : String? = null
    private lateinit var mContext :Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity.applicationContext
        arguments?.let {
            nickname = it.getString(NICKNAME_PARAM)
        }
        Log.d("Nickname", nickname)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_signup_credentials, container, false)
        val email = view.findViewById<EditText>(R.id.email_text)
        val password = view.findViewById<EditText>(R.id.password_text)

        val button = view.findViewById<Button>(R.id.next_in_credentials)

        button.setOnClickListener {
            if (email.text.toString() != "" && password.text.toString() != "") {
                var checkEmail = isValidEmail(email.text.toString())
                if(checkEmail) {
                    if(password.text.toString().length < 8){
                        Toast.makeText(mContext, "Password is too short (Min:8 character)", Toast.LENGTH_SHORT).show()
                    }else {
                        val args = Bundle().apply {
                            putString(NICKNAME_PARAM, nickname)
                            putString(EMAIL_PARAM, email.text.toString())
                            putString(PASSWORD_PARAM, password.text.toString())
                        }
                        val newFragment = SignupPersonalInfoFragment()
                        newFragment.arguments = args
                        val transaction = activity.supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.signup_frame, newFragment).addToBackStack("tag")
                        transaction.commit()
                    }
                }else{
                    Toast.makeText(mContext, "Invalid Email", Toast.LENGTH_SHORT).show()
                }
            }else if (email.text.toString() == ""){
                Toast.makeText(mContext, "Email is required", Toast.LENGTH_SHORT).show()
            }else if (password.text.toString() == ""){
                Toast.makeText(mContext, "Password is required", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }

    fun isValidEmail(target: String): Boolean {
        return if (target == "") false else android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()

    }
}
