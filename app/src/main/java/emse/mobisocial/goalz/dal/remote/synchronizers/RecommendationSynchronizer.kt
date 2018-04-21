package emse.mobisocial.goalz.dal.remote.synchronizers

import emse.mobisocial.goalz.dal.db.dao.RecommendationDao
import emse.mobisocial.goalz.dal.remote.ISynchronizer
import emse.mobisocial.goalz.model.RecommendationMinimal
import java.util.concurrent.Executor

/**
 * Created by MobiSocial EMSE Team on 4/20/2018.
 */
class RecommendationSynchronizer (
    private var recommendationDao: RecommendationDao,
    private var diskExecutor: Executor) : ISynchronizer<RecommendationMinimal> {

    init {
        diskExecutor.execute { recommendationDao.invalidateData() }
    }

    override fun addData(t: RecommendationMinimal) {
        diskExecutor.execute { recommendationDao.insertRecommendation(t) }
    }

    override fun updateData(t: RecommendationMinimal) {
        diskExecutor.execute { recommendationDao.updateRecommendation(t) }
    }

    override fun deleteData(t: RecommendationMinimal) {
        diskExecutor.execute { recommendationDao.deleteRecommendation(t) }
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: RecommendationSynchronizer? = null

        fun getInstance(recommendationDao: RecommendationDao, diskExecutor: Executor)
                : RecommendationSynchronizer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: RecommendationSynchronizer(recommendationDao, diskExecutor)
            }
        }
    }
}