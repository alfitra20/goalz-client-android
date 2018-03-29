package emse.mobisocial.goalz.dal.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.dal.db.dao.UserDao
import emse.mobisocial.goalz.model.UserMinimal
import emse.mobisocial.goalz.model.UserDetails
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserTemplate
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

    //Query
    override fun getUser(id : Int): LiveData<User> {
        return userDao.loadUser(id)
    }

    override fun getUsers(): LiveData<List<UserMinimal>> {
        return userDao.loadUsers()
    }

    override fun getUsersByTopic(topic : String): LiveData<List<UserMinimal>> {
        return userDao.loadUsersByTopic(topic)
    }

    //Insert
    override fun registerUser(userTemplate: UserTemplate) : LiveData<Int> {
        var result = MutableLiveData<Int>()
        executor.execute {
            var userMinimal = UserMinimal(
                    NEW_USER_ID, userTemplate.nickname, NEW_USER_RATING,
                    userTemplate.website, Date())

            var userId = userDao.insertUserMinimal(userMinimal).toInt()

            var userDetails = UserDetails(
                    userId, userTemplate.firstname, userTemplate.lastname, userTemplate.email,
                    userTemplate.age, userTemplate.gender)
            userDao.insertUserDetails(userDetails)

            result.postValue(userId)
        }

        return result
    }

    //Update
    override fun updateUser(user: User) : LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            userDao.updateUserMinimal(
                    UserMinimal(user.id, user.nickname, user.rating, user.website, user.registrationDate))
            userDao.updateUserDetails(
                    UserDetails(user.id, user.firstName, user.lastName, user.email, user.age, user.gender))
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    //Delete
    override fun deleteUser(id : Int) : LiveData<Boolean> {
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