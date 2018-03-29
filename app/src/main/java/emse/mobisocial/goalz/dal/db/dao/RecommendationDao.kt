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
    abstract fun loadRecommendation(id : Int) : LiveData<Recommendation>

    @Query("SELECT * FROM recommendations WHERE recommendation_id = :id")
    abstract fun loadRecommendationForDeleteOrUpdate(id : Int) : RecommendationMinimal

    @Transaction
    @Query("SELECT * FROM recommendations WHERE goal_id = :goalId")
    abstract fun loadRecommendationForGoal(goalId : Int) : LiveData<List<Recommendation>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Transaction
    @Query("SELECT recommendation_id, recommendations.goal_id, recommendations.user_id, " +
            "recommendations.resource_id, recommendations.title, recommendations.description, " +
            "req_time, rating FROM recommendations " +
            "JOIN goals ON recommendations.goal_id = goals.goal_id " +
            "WHERE goals.user_id = :userId")
    abstract fun loadRecommendationForUser(userId : Int) : LiveData<List<Recommendation>>

    @Transaction
    @Query("SELECT * FROM recommendations WHERE recommendations.user_id = :userId")
    abstract fun getRecommendationsForAuthor(userId: Int): LiveData<List<Recommendation>>

    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract fun insertRecommendation(recommendationMinimal: RecommendationMinimal) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRecommendationList(recommendationMinimal: List<RecommendationMinimal>)

    @Update
    abstract fun updateRecommendation(recommendationMinimal: RecommendationMinimal)

    @Delete
    abstract fun deleteRecommendation(recommendationMinimal: RecommendationMinimal)
}