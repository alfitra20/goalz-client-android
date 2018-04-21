package emse.mobisocial.goalz.dal.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import emse.mobisocial.goalz.model.Goal

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
@Dao
abstract class GoalDao {

    @Query("SELECT * FROM goals WHERE goal_id = :id")
    abstract fun loadGoal(id : String): LiveData<Goal>

    @Query("SELECT * FROM goals WHERE goal_id = :id")
    abstract fun loadGoalSync(id : String): Goal

    @Query("SELECT * FROM goals WHERE goal_id = :id")
    abstract fun loadGoalForDelete(id : String): Goal?

    @Query("SELECT * FROM goals")
    abstract fun loadGoals() : LiveData<List<Goal>>

    @Query("SELECT * FROM goals WHERE parent_id = :parentId")
    abstract fun loadSubgoals(parentId : String): LiveData<List<Goal>>

    @Query("SELECT * FROM goals WHERE user_id = :userId")
    abstract fun loadGoalsForUser(userId : String): LiveData<List<Goal>>

    @Query("SELECT * FROM goals WHERE topic = :topic")
    abstract fun loadGoalsByTopic(topic : String): LiveData<List<Goal>>

    @Query("SELECT * FROM goals WHERE title LIKE :formattedQuery OR topic LIKE :formattedQuery OR description LIKE :formattedQuery")
    abstract fun searchGoals(formattedQuery: String): LiveData<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGoal(goal: Goal)


    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateGoal(goal: Goal)


    @Delete
    abstract fun deleteGoal(goal: Goal)

    @Query("DELETE FROM goals")
    abstract fun invalidateData()
}