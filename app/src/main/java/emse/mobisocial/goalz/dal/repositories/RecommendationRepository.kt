package emse.mobisocial.goalz.dal.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import emse.mobisocial.goalz.dal.IRecommendationRepository
import emse.mobisocial.goalz.dal.db.dao.RecommendationDao
import emse.mobisocial.goalz.model.Recommendation
import emse.mobisocial.goalz.model.RecommendationMinimal
import emse.mobisocial.goalz.model.RecommendationTemplate
import java.util.concurrent.Executor

private const val NEW_RECOMMENDATION_ID = 0
private const val NEW_RECOMMENDATION_RATING = 0.0

/**
 * Created by MobiSocial EMSE Team on 3/29/2018.
 *
 * Note: This class is a singleton. The companion object can be found at the bottom of the file
 */
class RecommendationRepository (
        private var executor : Executor,
        private var recommendationDao: RecommendationDao)
    : IRecommendationRepository {

    //Query
    override fun getRecommendation(id: Int): LiveData<Recommendation> {
        return recommendationDao.loadRecommendation(id)
    }

    override fun getRecommendationsForGoal(goalId: Int): LiveData<List<Recommendation>> {
        return recommendationDao.loadRecommendationForGoal(goalId)
    }

    override fun getRecommendationsForUser(userId: Int): LiveData<List<Recommendation>> {
        return recommendationDao.loadRecommendationForUser(userId)
    }

    override fun getRecommendationsForAuthor(userId: Int): LiveData<List<Recommendation>> {
        return recommendationDao.getRecommendationsForAuthor(userId)
    }

    //Insert
    override fun addRecommendation(template: RecommendationTemplate): LiveData<Int> {
        var result = MutableLiveData<Int>()
        executor.execute {
            val recommendation = RecommendationMinimal(
                    NEW_RECOMMENDATION_ID,
                    template.resourceId,
                    template.goalId,
                    template.userId,
                    template.title,
                    template.description,
                    template.reqTime,
                    NEW_RECOMMENDATION_RATING)

            val id = recommendationDao.insertRecommendation(recommendation).toInt()
            result.postValue(id)
        }

        return result
    }

    //Update
    override fun rateRecommendation(id: Int, rating: Double): LiveData<Boolean> {
        //TODO: Update this method to propagate the rating operation to refered Resource and Author
        var result = MutableLiveData<Boolean>()
        executor.execute {
            var recommendation = recommendationDao.loadRecommendationForDeleteOrUpdate(id)
            if (recommendation!= null) {
                recommendation.rating = rating
                recommendationDao.updateRecommendation(recommendation)
            }
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    //Delete
    override fun deleteRecommendation(id: Int): LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            var recommendation = recommendationDao.loadRecommendationForDeleteOrUpdate(id)
            if (recommendation!= null) {
                recommendationDao.deleteRecommendation(recommendation)
            }
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: RecommendationRepository? = null

        fun getInstance(executor: Executor, recommendationDao: RecommendationDao)
                : RecommendationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: RecommendationRepository(executor, recommendationDao)
            }
        }
    }
}