package emse.mobisocial.goalz.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.UserMinimal
import emse.mobisocial.goalz.ui.viewModels.FABGoalResourceVM
import kotlinx.android.synthetic.main.fragment_signup_nickname_fragment.*
import java.util.ArrayList


private const val NICKNAME_PARAM = "nickname"

class SignupNicknameFragment: Fragment() {

    private lateinit var model : FABGoalResourceVM
    private lateinit var mContext :Context
    private var nickname:String? = null
    private lateinit var usersList : ArrayList<UserMinimal>

    /*override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(NICKNAME_PARAM, nickname)
        super.onSaveInstanceState(outState)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity.applicationContext

        /*if(savedInstanceState != null){
            nickname_text.setText(savedInstanceState.getString(NICKNAME_PARAM).toString())
        }*/

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(R.layout.fragment_signup_nickname_fragment, container, false)
        val button = view.findViewById(R.id.next_in_nickname)as Button

        button.setOnClickListener {
            if(nickname_text.text.toString() != ""){
                nickname = nickname_text.text.toString()
                val args = Bundle().apply {
                    putString(NICKNAME_PARAM, nickname)
                }
                val newFragment = SignupCredentialsFragment()
                newFragment.arguments = args
                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.signup_frame, newFragment).addToBackStack("tag")
                transaction.commit()
            }else{
                Toast.makeText(mContext, "Nickname required", Toast.LENGTH_SHORT).show()
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
}
