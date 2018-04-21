package emse.mobisocial.goalz.test.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.dal.db.converter.LocationConverter
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.GoalTemplate
import java.util.*


private const val NEW_GOAL_USER_ID = "testUserId"
private const val NEW_GOAL_PARENT_ID = "testParentId"
private const val NEW_GOAL_TITLE = "Learn guitar"
private const val NEW_GOAL_TOPIC = "Music"
private const val NEW_GOAL_DESCRIPTION = "Niceeee"
private val NEW_GOAL_LOCATION = LocationConverter.toLocation("65.059666,25.462197")

private const val UPDATED_GOAL_TITLE = "Updated Goal"
private const val UPDATED_GOAL_TOPIC = "new"
private const val UPDATED_GOAL_DESCRIPTION = "Boring goal"
private const val UPDATED_GOAL_STATUS = 10
private val UPDATED_GOAL_DEADLINE = Date()


/**
 * Created by MobiSocial EMSE Team on 3/28/2018.
 */
class GoalTestViewModel (application: Application) : AndroidViewModel(application){

    private val goalRepository : IGoalRepository = (application as GoalzApp).goalRepository
    private var goalListDb: MutableLiveData<LiveData<List<Goal>>> = MutableLiveData<LiveData<List<Goal>>>()
    val resourceList: LiveData<List<Goal>> = Transformations.switchMap(goalListDb){ it }

    var selectedGoal : Goal? = null

    init {
        goalListDb.postValue(goalRepository.getGoals())
    }

    fun deleteSelectedGoal() : LiveData<DalResponse>?{
        val x = selectedGoal
        if(x != null) {
            return goalRepository.deleteGoal(x.id)
        }
        return null
    }

    fun updateSelectedGoal() : LiveData<DalResponse>? {
        val goal = selectedGoal
        if (goal != null) {
            goal.title = UPDATED_GOAL_TITLE
            goal.topic = UPDATED_GOAL_TOPIC
            goal.description = UPDATED_GOAL_DESCRIPTION
            goal.status = UPDATED_GOAL_STATUS
            goal.deadline = UPDATED_GOAL_DEADLINE

            return goalRepository.updateGoal(goal)
        }
        return null
    }

    fun addGoal() : LiveData<DalResponse> {
        val newGoal = GoalTemplate(
                NEW_GOAL_USER_ID,
                NEW_GOAL_PARENT_ID,
                NEW_GOAL_TITLE,
                NEW_GOAL_TOPIC,
                NEW_GOAL_DESCRIPTION,
                NEW_GOAL_LOCATION)
        return goalRepository.addGoal(newGoal)
    }

    fun searchByTopic(topic : String) {
        if (topic == ""){
            goalListDb.postValue(goalRepository.getGoals())
        }
        else {
            goalListDb.postValue(goalRepository.getGoalsByTopic(topic))
        }
    }

    fun searchByUser(id : String) {
        goalListDb.postValue(goalRepository.getGoalsForUser(id))
    }

    fun searchSubgoals(id : String) {
        goalListDb.postValue(goalRepository.getSubgoals(id))
    }
}