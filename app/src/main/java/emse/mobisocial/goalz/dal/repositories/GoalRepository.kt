package emse.mobisocial.goalz.dal.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.database.*
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.dal.db.dao.GoalDao
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.GoalTemplate
import java.util.concurrent.Executor
import emse.mobisocial.goalz.dal.remote.data.GoalFb
import com.google.firebase.database.DatabaseReference
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus


/**
 * Created by MobiSocial EMSE Team on 3/28/2018.
 *
 * Note: This class is a singleton. The companion object can be found at the bottom of the file
 */

class GoalRepository(
        private var goalDao: GoalDao,
        private var diskExecutor : Executor,
        private var networkExecutor : Executor
        )
    : IGoalRepository{

    private val remoteGoalTable : DatabaseReference
            = FirebaseDatabase.getInstance().reference.child("goals")

    // Application accessible methods
    override fun getGoal(id: String): LiveData<Goal> {
        return goalDao.loadGoal(id)
    }

    override fun getGoals(): LiveData<List<Goal>> {
        return goalDao.loadGoals()
    }

    override fun searchGoals(formattedQuery: String): LiveData<List<Goal>> {
        return goalDao.searchGoals(formattedQuery)
    }

    override fun searchGoalsForUser(formattedQuery: String, userId: String): LiveData<List<Goal>> {
        return goalDao.searchGoalsForUser(formattedQuery, userId)
    }

    override fun getSubgoals(parentId: String): LiveData<List<Goal>> {
        return goalDao.loadSubgoals(parentId)
    }

    override fun getGoalsForUser(userId: String): LiveData<List<Goal>> {
        return goalDao.loadGoalsForUser(userId)
    }

    override fun getGoalsByTopic(topic: String): LiveData<List<Goal>> {
        return goalDao.loadGoalsByTopic(topic)
    }

    override fun addGoal(template: GoalTemplate): LiveData<DalResponse> {
        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        networkExecutor.execute {
            val newId = remoteGoalTable.push().key
            val goalFb = GoalFb(template)
            remoteGoalTable.child(newId).setValue(goalFb , {
                error, _ -> run {
                    if (error == null) {
                        diskExecutor.execute {
                            var newGoal = goalFb.toEntity(newId)
                            goalDao.insertGoal(newGoal)
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

    override fun updateGoal(goal: Goal): LiveData<DalResponse> {
        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        networkExecutor.execute {
            val updateInfo = HashMap<String, Any?>()
            updateInfo["title"] = goal.title
            updateInfo["topic"] = goal.topic
            updateInfo["description"] = goal.description
            updateInfo["latitude"] = goal.location.latitude
            updateInfo["longitude"] = goal.location.longitude
            var deadlineTmp = goal.deadline
            var newDeadline = if (deadlineTmp != null) deadlineTmp.time / 1000 else null
            updateInfo["deadline"] = newDeadline

            remoteGoalTable.child(goal.id).updateChildren(updateInfo, {
                error, _ -> run {
                    if (error == null) {
                        diskExecutor.execute {
                            goalDao.updateGoal(goal)
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

    override fun deleteGoal(id: String): LiveData<DalResponse> {
        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        if(id == "")
            result.postValue(DalResponse(DalResponseStatus.SUCCESS, null))
        else {
            networkExecutor.execute {
                remoteGoalTable.child(id).removeValue({ error, _ ->
                    run {
                        if (error == null) {
                            diskExecutor.execute {
                                var goal = goalDao.loadGoalForDelete(id)
                                if (goal != null) {
                                    goalDao.deleteGoal(goal)
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
        @Volatile private var INSTANCE: GoalRepository? = null

        fun getInstance(goalDao: GoalDao, diskExecutor: Executor, networkExecutor: Executor)
                : GoalRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: GoalRepository(goalDao, diskExecutor, networkExecutor)
            }
        }
    }
}