package emse.mobisocial.goalz.dal

import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.GoalTemplate

/**
 * Created by MobiSocial EMSE Team on 3/28/2018.
 */
interface IGoalRepository {

    //Query
    fun getGoal(id : Int) : LiveData<Goal>

    fun getGoals() : LiveData<List<Goal>>

    fun getSubgoals(parentId : Int) : LiveData<List<Goal>>

    fun getGoalsForUser(userId : Int) : LiveData<List<Goal>>

    fun getGoalsByTopic(topic : String) : LiveData<List<Goal>>

    //Insert
    fun addGoal(template: GoalTemplate) : LiveData<Int>

    //Update
    fun updateGoal(goal: Goal) : LiveData<Boolean>

    //Delete
    fun deleteGoal(id: Int) : LiveData<Boolean>
}