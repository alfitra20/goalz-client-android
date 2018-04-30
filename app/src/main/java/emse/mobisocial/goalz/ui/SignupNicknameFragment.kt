package emse.mobisocial.goalz.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.UserMinimal
import emse.mobisocial.goalz.ui.viewModels.CreateGoalViewModel
import kotlinx.android.synthetic.main.fragment_signup_nickname_fragment.*
import java.util.ArrayList

private const val NICKNAME_PARAM = "nickname"

class SignupNicknameFragment: Fragment() {

    private lateinit var model : CreateGoalViewModel
    private lateinit var mContext :Context
    private var nickname:String? = null
    private lateinit var usersList : ArrayList<UserMinimal>
    private lateinit var mSnackbar: Snackbar
    var redColor = Color.parseColor("#FF6347")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity.applicationContext

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(R.layout.fragment_signup_nickname_fragment, container, false)
        val button = view.findViewById(R.id.next_in_nickname) as Button

        button.setOnClickListener {
            if(nickname_text.text.toString() != ""){
                nickname = nickname_text.text.toString().trim()
                val args = Bundle()
                args.putString(NICKNAME_PARAM, nickname)
                val newFragment = SignupPersonalInfoFragment()
                newFragment.arguments = args
                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.signup_frame, newFragment).addToBackStack("tag")
                transaction.commit()
            }else{
                launchSnackbar("Nickname required")
            }
        }
        return view
    }

    private fun launchSnackbar(title: String) {
        mSnackbar = Snackbar.make(signup_nickname_layout, title, Snackbar.LENGTH_LONG)
        mSnackbar.view.background = ColorDrawable(redColor)
        mSnackbar.show()
    }
}
