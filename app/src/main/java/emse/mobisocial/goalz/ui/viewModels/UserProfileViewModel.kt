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

    fun modifyUserData(nickname : String, firstname:String, lastname:String, userAge:Int, website:String, gender:Gender ): LiveData<DalResponse>? {
        val updateState = userData.value ?: return null

        updateState.nickname = nickname
        updateState.firstName = firstname
        updateState.lastName = lastname
        updateState.age = userAge
        updateState.website = website
        updateState.gender = gender

        return userRepository.updateUser(updateState)
    }

    fun registerNewUser(newUserData : UserTemplate ): LiveData<DalResponse> {
        return userRepository.registerUser(newUserData)
    }
}