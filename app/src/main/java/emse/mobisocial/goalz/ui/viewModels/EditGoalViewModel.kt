package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.IGoalRepository
import java.util.*

/**
 * Created by MobiSocial EMSE Team on 4/29/2018.
 */
class EditGoalViewModel(application: Application, val goalId : String): AndroidViewModel(application) {

    private val goalRepository: IGoalRepository = (application as GoalzApp).goalRepository

    val goal = goalRepository.getGoal(goalId)

    fun updateGoal(title : String, topic : String, description : String, deadline : Date) : LiveData<DalResponse>? {
        val updateState = goal.value ?: return null

        updateState.title = title
        updateState.topic = topic
        updateState.deadline = deadline
        updateState.description = description

        return goalRepository.updateGoal(updateState)
    }
}