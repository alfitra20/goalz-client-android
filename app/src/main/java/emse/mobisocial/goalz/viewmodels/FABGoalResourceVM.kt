package emse.mobisocial.goalz.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.GoalTemplate
import emse.mobisocial.goalz.model.ResourceTemplate


class FABGoalResourceVM (application: Application) : AndroidViewModel(application){
    private val goalRepository : IGoalRepository = (application as GoalzApp).goalRepository
    private val resourceRepository : IResourceRepository = (application as GoalzApp).resourceRepository

    lateinit var userGoalsList: LiveData<List<Goal>>

    fun setUser(userId: Int){
        userGoalsList = goalRepository.getGoalsForUser(userId)
    }

    fun addGoal(newGoal:GoalTemplate) : LiveData<Int> {
        return goalRepository.addGoal(newGoal)
    }

    fun addResource(newResource: ResourceTemplate){
        resourceRepository.addResource(newResource)
    }

}