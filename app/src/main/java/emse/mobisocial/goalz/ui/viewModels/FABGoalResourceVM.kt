package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
<<<<<<< HEAD
import emse.mobisocial.goalz.dal.DalResponse
=======
>>>>>>> c333e9a2669f572a10d4f56c6632fe989c1ad2dd
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.GoalTemplate
import emse.mobisocial.goalz.model.ResourceTemplate


class FABGoalResourceVM (application: Application) : AndroidViewModel(application){
    private val goalRepository : IGoalRepository = (application as GoalzApp).goalRepository
    private val resourceRepository : IResourceRepository = (application as GoalzApp).resourceRepository

    lateinit var userGoalsList: LiveData<List<Goal>>

<<<<<<< HEAD
    fun setUser(userId: String){
        userGoalsList = goalRepository.getGoalsForUser(userId)
    }

    fun addGoal(newGoal:GoalTemplate) : LiveData<DalResponse> {
=======
    fun setUser(userId: Int){
        userGoalsList = goalRepository.getGoalsForUser(userId)
    }

    fun addGoal(newGoal:GoalTemplate) : LiveData<Int> {
>>>>>>> c333e9a2669f572a10d4f56c6632fe989c1ad2dd
        return goalRepository.addGoal(newGoal)
    }

    fun addResource(newResource: ResourceTemplate){
        resourceRepository.addResource(newResource)
    }

}