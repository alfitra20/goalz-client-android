package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserTemplate
import emse.mobisocial.goalz.util.Gender

class UserProfileViewModel(application: Application): AndroidViewModel(application) {

    private val userRepository: IUserRepository = (application as GoalzApp).userRepository
    private val goalRepository: IGoalRepository = (application as GoalzApp).goalRepository
    private val resourceRepository: IResourceRepository = (application as GoalzApp).resourceRepository

    lateinit var userData: LiveData<User>
    lateinit var usersGoal: LiveData<List<Goal>>
    lateinit var usersResource: LiveData<List<Resource>>

    fun getUsersGoal(userId: String){
        usersGoal = goalRepository.getGoalsForUser(userId)
    }

    fun getUsersResource(userId: String){
        usersResource = resourceRepository.getLibraryForUser(userId)
    }

    fun getUser(userId: String){
        userData = userRepository.getUser(userId)
    }

    fun modidyUserData(userId: String, newUserData : UserTemplate ): LiveData<DalResponse> {
        var userData: LiveData<User> = userRepository.getUser(userId)
        var user = userData.value
        if (user != null) {
            user.website = newUserData.website
            user.firstName = newUserData.firstname
            user.lastName = newUserData.lastname
            user.age = newUserData.age!!
            user.gender = newUserData.gender!!
        }
        return userRepository.updateUser(user!!)
    }

}