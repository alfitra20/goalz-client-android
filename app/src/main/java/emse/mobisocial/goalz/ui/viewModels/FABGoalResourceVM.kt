package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.model.*


class FABGoalResourceVM (application: Application) : AndroidViewModel(application){
    private val goalRepository : IGoalRepository = (application as GoalzApp).goalRepository
    private val userRepository : IUserRepository = (application as GoalzApp).userRepository
    private val resourceRepository : IResourceRepository = (application as GoalzApp).resourceRepository

    lateinit var userGoalsList: LiveData<List<Goal>>

    fun setUser(userId: String){
        userGoalsList = goalRepository.getGoalsForUser(userId)
    }

    fun addGoal(newGoal:GoalTemplate) : LiveData<DalResponse> {
        return goalRepository.addGoal(newGoal)
    }

    fun addResource(newResource: ResourceTemplate) : LiveData<DalResponse> {
        return resourceRepository.addResource(newResource)
    }

    fun addUser(newUser:UserTemplate):LiveData<DalResponse>{
        return userRepository.registerUser(newUser)
    }

}