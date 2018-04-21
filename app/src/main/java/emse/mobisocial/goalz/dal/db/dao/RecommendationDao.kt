package emse.mobisocial.goalz.dal.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import emse.mobisocial.goalz.model.Recommendation
import emse.mobisocial.goalz.model.RecommendationMinimal

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
@Dao
abstract class RecommendationDao {

    @Transaction
    @Query("SELECT * FROM recommendations WHERE recommendation_id = :id")
    abstract fun loadRecommendation(id : String) : LiveData<Recommendation>

    @Query("SELECT * FROM recommendations WHERE recommendation_id = :id")
    abstract fun loadRecommendationForDeleteOrUpdate(id : String) : RecommendationMinimal?

    @Transaction
    @Query("SELECT * FROM recommendations WHERE goal_id = :goalId")
    abstract fun loadRecommendationForGoal(goalId : String) : LiveData<List<Recommendation>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Transaction
    @Query("SELECT recommendation_id, recommendations.goal_id, recommendations.user_id, " +
            "recommendations.resource_id, recommendations.title, recommendations.description, " +
            "req_time, rating FROM recommendations " +
            "JOIN goals ON recommendations.goal_id = goals.goal_id " +
            "WHERE goals.user_id = :userId")
    abstract fun loadRecommendationForUser(userId : String) : LiveData<List<Recommendation>>

    @Transaction
    @Query("SELECT * FROM recommendations WHERE recommendations.user_id = :userId")
    abstract fun getRecommendationsForAuthor(userId: String): LiveData<List<Recommendation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRecommendation(recommendationMinimal: RecommendationMinimal)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateRecommendation(recommendationMinimal: RecommendationMinimal)

    @Delete
    abstract fun deleteRecommendation(recommendationMinimal: RecommendationMinimal)

    @Query("DELETE FROM recommendations")
    abstract fun invalidateData()
}