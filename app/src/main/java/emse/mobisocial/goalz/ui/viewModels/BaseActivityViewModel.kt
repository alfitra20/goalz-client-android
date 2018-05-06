package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.model.User

/**
 * Created by MobiSocial EMSE Team on 5/3/2018.
 */
class BaseActivityViewModel(application: Application): AndroidViewModel(application) {
    private val userRepository: IUserRepository = (application as GoalzApp).userRepository

    var userData: LiveData<User>? = null

    fun initialize(userId: String?){
        if (userId != null) {
            userData = userRepository.getUser(userId)
        }
    }
}