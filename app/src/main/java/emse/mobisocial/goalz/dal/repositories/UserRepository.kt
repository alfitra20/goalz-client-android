package emse.mobisocial.goalz.dal.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.dal.db.dao.UserDao
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserDetails
import emse.mobisocial.goalz.model.UserInfo
import java.util.*
import java.util.concurrent.Executor

private const val NEW_USER_ID = 0
private const val NEW_USER_RATING : Double = 0.0

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 *
 * Note: This class is a singleton. The companion object can be found at the bottom of the file
 */
class UserRepository private constructor(
        private var executor : Executor, private var userDao: UserDao) : IUserRepository {

    //Insert
    override fun registerUser(userInfo: UserInfo) : LiveData<Int> {
        var result = MutableLiveData<Int>()
        executor.execute {
            var userBasic = User(
                    NEW_USER_ID, userInfo.nickname, NEW_USER_RATING,
                    userInfo.website, Date())

            var userId = userDao.insertUsers(userBasic).toInt()

            var userDetails = UserDetails(
                    userId, userInfo.firstname, userInfo.lastname, userInfo.email,
                    userInfo.age, userInfo.gender)
            userDao.insertUserDetails(userDetails)

            result.postValue(userId)
        }

        return result
    }

    //Update
    override fun updateUser(user: User) : LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            userDao.updateUser(user)
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    override fun updateUser(user: User, userDetails: UserDetails) : LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            userDao.updateUser(user)
            userDao.updateUserDetails(userDetails)
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    override fun updateUserDetails(userDetails: UserDetails) : LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            userDao.updateUserDetails(userDetails)
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    //Delete
    override fun deleteUser(user: User) : LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            userDao.deleteUser(user)
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    override fun deleteUserById(id : Int) : LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            var user = userDao.loadUserForDelete(id)
            if (user!= null) {
                userDao.deleteUser(user)
            }
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    //Query
    override fun getUsers(): LiveData<List<User>> {
        return userDao.loadUsers()
    }

    override fun getUser(id : Int): LiveData<User> {
        return userDao.loadUser(id)
    }

    override fun getUserDetails(id : Int): LiveData<UserDetails> {
        return userDao.loadUserDetails(id)
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: UserRepository? = null

        fun getInstance(executor: Executor, userDao: UserDao): UserRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: UserRepository(executor, userDao)
            }
        }
    }
}