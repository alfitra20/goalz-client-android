package emse.mobisocial.goalz.dal.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.dal.db.dao.GoalDao
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.GoalTemplate
import java.util.concurrent.Executor

/**
 * Created by MobiSocial EMSE Team on 3/28/2018.
 *
 * Note: This class is a singleton. The companion object can be found at the bottom of the file
 */

private const val NEW_GOAL_ID = 0

class GoalRepository(
        private var executor : Executor,
        private var goalDao: GoalDao)
    : IGoalRepository {

    override fun getGoal(id: Int): LiveData<Goal> {
        return goalDao.loadGoal(id)
    }

    override fun getGoals(): LiveData<List<Goal>> {
        return goalDao.loadGoals()
    }

    override fun getSubgoals(parentId: Int): LiveData<List<Goal>> {
        return goalDao.loadSubgoals(parentId)
    }

    override fun getGoalsForUser(userId: Int): LiveData<List<Goal>> {
        return goalDao.loadGoalsForUser(userId)
    }

    override fun getGoalsByTopic(topic: String): LiveData<List<Goal>> {
        return goalDao.loadGoalsByTopic(topic)
    }

    override fun addGoal(template: GoalTemplate): LiveData<Int> {
        var result = MutableLiveData<Int>()
        executor.execute {
            val goal = Goal(
                    NEW_GOAL_ID, template.userId, template.parentId,
                    template.title, template.topic, template.description,
                    template.location, template.status, template.deadline)

            val id = goalDao.insertGoal(goal).toInt()
            result.postValue(id)
        }

        return result
    }

    override fun updateGoal(goal: Goal): LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            goalDao.updateGoal(goal)
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    override fun deleteGoal(id: Int): LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            var goal = goalDao.loadGoalForDelete(id)
            if (goal!= null) {
                goalDao.deleteGoal(goal)
            }
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: GoalRepository? = null

        fun getInstance(executor: Executor, goalDao: GoalDao): GoalRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: GoalRepository(executor, goalDao)
            }
        }
    }
}