package emse.mobisocial.goalz.notification

import android.app.Service
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IUserRepository

/**
 * Created by MobiSocial EMSE Team on 4/30/2018.
 */
class NotificationRegistrationService : FirebaseInstanceIdService() {

    private var userId : String? = null
    private lateinit var userRepository : IUserRepository

    override fun onCreate() {
        Log.d("TOKEN SERVICE", "Service created")
        userRepository = (application as GoalzApp).userRepository

        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null){
                userId = user.uid
                attachUserToken(userId!!)
            }
        }
    }

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        if (userId != null){
            attachUserToken(userId!!)
        }
        Log.d("TOKEN SERVICE", "Refreshed token: " + refreshedToken!!)
    }

    private fun attachUserToken(userId : String){
        val token = FirebaseInstanceId.getInstance().token ?: return
        userRepository.setMessagingToken(userId, token)
    }


}