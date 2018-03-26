package emse.mobisocial.goalz.dal.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.dal.db.dao.UserDao
import emse.mobisocial.goalz.model.UserBasic
import emse.mobisocial.goalz.model.UserDetails
import emse.mobisocial.goalz.model.UserInfo
import java.util.*
import java.util.concurrent.Executor

/**
 * Created by dtoni on 3/25/2018.
 *
 * Note: This class is a singleton. The companion object can be found at the bottom of the file
 */

private const val NEW_USER_ID = 0
private const val NEW_USER_RATING : Double = 0.0

class UserRepository private constructor(private var executor : Executor, private var userDao: UserDao) : IUserRepository {

    //Insert
    override fun registerUser(userInfo: UserInfo) : LiveData<Int> {
        var result = MutableLiveData<Int>()
        executor.execute {
            var userBasic = UserBasic(
                    NEW_USER_ID, userInfo.nickname, NEW_USER_RATING,
                    userInfo.website, Date())

            var userId = userDao.insertUserBasicInfo(userBasic).toInt()

            var userDetails = UserDetails(
                    userId, userInfo.firstname, userInfo.lastname, userInfo.email,
                    userInfo.age, userInfo.gender)
            userDao.insertUserDetailsInfo(userDetails)

            result.postValue(userId)
        }

        return result
    }

    //Update
    override fun updateUser(userBasic: UserBasic) : LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            userDao.updateUserBasicInfo(userBasic)
            result.postValue(true) //TODO: This should be changed based on succes/fail logic
        }

        return result
    }

    override fun updateUser(userBasic: UserBasic, userDetails: UserDetails) : LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            userDao.updateUserBasicInfo(userBasic)
            userDao.updateUserDetailsInfo(userDetails)
            result.postValue(true) //TODO: This should be changed based on succes/fail logic
        }

        return result
    }

    override fun updateUserDetails(userDetails: UserDetails) : LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            userDao.updateUserDetailsInfo(userDetails)
            result.postValue(true) //TODO: This should be changed based on succes/fail logic
        }

        return result
    }

    //Delete
    override fun deleteUser(userBasic: UserBasic) : LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            userDao.deleteUser(userBasic)
            result.postValue(true) //TODO: This should be changed based on succes/fail logic
        }

        return result
    }

    //Query
    override fun getUsers(): LiveData<List<UserBasic>> {
        return userDao.loadUsers()
    }

    override fun getUserBasic(id : Int): LiveData<UserBasic> {
        return userDao.loadUserBasicById(id)
    }

    override fun getUserDetails(id : Int): LiveData<UserDetails> {
        return userDao.loadUserDetailsById(id)
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: UserRepository? = null

        fun getInstance(userDao: UserDao, executor: Executor): UserRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: UserRepository(executor, userDao)
            }
        }
    }
}