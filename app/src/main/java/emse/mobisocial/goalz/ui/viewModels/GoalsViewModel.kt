package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.model.Goal

/**
 * Created by MobiSocial EMSE Team on 5/1/2018.
 */
class GoalsViewModel(application: Application): AndroidViewModel(application) {

    private val goalRepository: IGoalRepository = (application as GoalzApp).goalRepository

    lateinit var goals: LiveData<List<Goal>>
    lateinit var userId : String
    lateinit var userNickname : String

    fun initialize(userId : String, userNickname : String) {
        this.userId = userId
        this.userNickname = userNickname
        goals = goalRepository.getGoalsForUser(userId)
    }

}