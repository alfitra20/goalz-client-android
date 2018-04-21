package emse.mobisocial.goalz.dal.remote.synchronizers

import emse.mobisocial.goalz.dal.db.dao.UserDao
import emse.mobisocial.goalz.dal.remote.ISynchronizer
import emse.mobisocial.goalz.model.User
import java.util.concurrent.Executor

/**
 * Created by MobiSocial EMSE Team on 4/20/2018.
 */
class UserSynchronizer (
        private var userDao: UserDao,
        private var diskExecutor: Executor) : ISynchronizer<User> {

    override fun addData(t: User) {
        diskExecutor.execute {
            userDao.insertUserMinimal(t.getUserMinimal())
            userDao.insertUserDetails(t.getUserDetails())
        }
    }

    override fun updateData(t: User) {
        diskExecutor.execute {
            userDao.updateUserMinimal(t.getUserMinimal())
            userDao.updateUserDetails(t.getUserDetails())
        }
    }

    override fun deleteData(t: User) {
        diskExecutor.execute {
            userDao.deleteUser(t.getUserMinimal())
        }
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: UserSynchronizer? = null

        fun getInstance(userDao: UserDao, diskExecutor: Executor) : UserSynchronizer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserSynchronizer(userDao, diskExecutor)
            }
        }
    }
}