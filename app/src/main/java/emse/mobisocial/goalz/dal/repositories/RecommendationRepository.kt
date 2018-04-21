package emse.mobisocial.goalz.dal.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.dal.IRecommendationRepository
import emse.mobisocial.goalz.dal.db.dao.RecommendationDao
import emse.mobisocial.goalz.dal.remote.data.RecommendationFb
import emse.mobisocial.goalz.model.Recommendation
import emse.mobisocial.goalz.model.RecommendationTemplate
import java.util.concurrent.Executor

/**
 * Created by MobiSocial EMSE Team on 3/29/2018.
 *
 * Note: This class is a singleton. The companion object can be found at the bottom of the file
 */
class RecommendationRepository (
        private var recommendationDao: RecommendationDao,
        private var diskExecutor: Executor,
        private var networkExecutor: Executor
        )
    : IRecommendationRepository {

    private val remoteRecommendationTable : DatabaseReference
            = FirebaseDatabase.getInstance().reference.child("recommendations")

    //Query
    override fun getRecommendation(id: String): LiveData<Recommendation> {
        return recommendationDao.loadRecommendation(id)
    }

    override fun getRecommendationsForGoal(goalId: String): LiveData<List<Recommendation>> {
        return recommendationDao.loadRecommendationForGoal(goalId)
    }

    override fun getRecommendationsForUser(userId: String): LiveData<List<Recommendation>> {
        return recommendationDao.loadRecommendationForUser(userId)
    }

    override fun getRecommendationsForAuthor(userId: String): LiveData<List<Recommendation>> {
        return recommendationDao.getRecommendationsForAuthor(userId)
    }

    //Insert
    override fun addRecommendation(template: RecommendationTemplate) : LiveData<DalResponse> {
        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        networkExecutor.execute {
            val newId = remoteRecommendationTable.push().key
            val recommendationFb = RecommendationFb(template)
            remoteRecommendationTable.child(newId).setValue(recommendationFb , {
                error, _ -> run {
                if (error == null) {
                    diskExecutor.execute {
                        var newRecommendation = recommendationFb.toEntity(newId)
                        recommendationDao.insertRecommendation(newRecommendation)
                        result.postValue(DalResponse(DalResponseStatus.SUCCESS, newId))
                    }
                }
                else {
                    result.postValue(DalResponse(DalResponseStatus.FAIL, null))
                }
            }
            })
        }
        return result
    }

    //Update
    override fun rateRecommendation(id: String, rating: Double): LiveData<DalResponse> {
        //TODO: Update this method to propagate the rating operation to refered Resource and Author
        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        networkExecutor.execute {
            val updateInfo = HashMap<String, Any?>()
            updateInfo["rating"] = rating

            remoteRecommendationTable.child(id).updateChildren(updateInfo, {
                error, _ -> run {
                    if (error == null) {
                        diskExecutor.execute {
                            var recommendation = recommendationDao.loadRecommendationForDeleteOrUpdate(id)
                            if (recommendation!= null) {
                                recommendation.rating = rating
                                recommendationDao.updateRecommendation(recommendation)
                            }
                            result.postValue(DalResponse(DalResponseStatus.SUCCESS, null))
                        }
                    }
                    else {
                        result.postValue(DalResponse(DalResponseStatus.FAIL, null))
                    }
                }
            })
        }

        return result
    }

    //Delete
    override fun deleteRecommendation(id: String): LiveData<DalResponse> {
        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        if(id == "")
            result.postValue(DalResponse(DalResponseStatus.SUCCESS, null))
        else {
            networkExecutor.execute {
                remoteRecommendationTable.child(id).removeValue({ error, _ ->
                    run {
                        if (error == null) {
                            diskExecutor.execute {
                                var recommendation = recommendationDao.loadRecommendationForDeleteOrUpdate(id)
                                if (recommendation != null) {
                                    recommendationDao.deleteRecommendation(recommendation)
                                }
                                result.postValue(DalResponse(DalResponseStatus.SUCCESS, null))
                            }
                        } else {
                            result.postValue(DalResponse(DalResponseStatus.FAIL, null))
                        }
                    }
                })
            }
        }

        return result
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: RecommendationRepository? = null

        fun getInstance(recommendationDao: RecommendationDao,
                        diskExecutor: Executor, networkExecutor : Executor)
                : RecommendationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: RecommendationRepository(
                                recommendationDao, diskExecutor, networkExecutor)
            }
        }
    }
}