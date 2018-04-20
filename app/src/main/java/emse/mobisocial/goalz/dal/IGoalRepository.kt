package emse.mobisocial.goalz.dal

import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.GoalTemplate

/**
 * Created by MobiSocial EMSE Team on 3/28/2018.
 */
interface IGoalRepository {

    //Query
    fun getGoal(id : String) : LiveData<Goal>

    fun getGoals() : LiveData<List<Goal>>

    fun getSubgoals(parentId : String) : LiveData<List<Goal>>

    fun getGoalsForUser(userId : String) : LiveData<List<Goal>>

    fun getGoalsByTopic(topic : String) : LiveData<List<Goal>>

    //Insert
    fun addGoal(template: GoalTemplate) : LiveData<DalResponse>

    //Update
    fun updateGoal(goal: Goal) : LiveData<DalResponse>

    //Delete
    fun deleteGoal(id: String) : LiveData<DalResponse>
}