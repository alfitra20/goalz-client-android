package emse.mobisocial.goalz.dal.remote.synchronizers

import emse.mobisocial.goalz.dal.db.dao.GoalDao
import emse.mobisocial.goalz.dal.remote.ISynchronizer
import emse.mobisocial.goalz.model.Goal
import java.util.concurrent.Executor

/**
 * Created by MobiSocial EMSE Team on 4/20/2018.
 */
class GoalSynchronizer(
        private var goalDao: GoalDao,
        private var diskExecutor: Executor) : ISynchronizer<Goal>{

    init {
        diskExecutor.execute { goalDao.invalidateData() }
    }

    override fun addData(t: Goal) {
        diskExecutor.execute { goalDao.insertGoal(t) }
    }

    override fun updateData(t: Goal) {
        diskExecutor.execute { goalDao.updateGoal(t) }
    }

    override fun deleteData(t : Goal) {
        diskExecutor.execute { goalDao.deleteGoal(t) }
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: GoalSynchronizer? = null

        fun getInstance(goalDao: GoalDao, diskExecutor: Executor) : GoalSynchronizer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GoalSynchronizer(goalDao, diskExecutor)
            }
        }
    }
}