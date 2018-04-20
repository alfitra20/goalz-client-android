package emse.mobisocial.goalz.dal.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.dal.db.dao.UserDao
import emse.mobisocial.goalz.model.UserMinimal
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserTemplate
import java.util.concurrent.Executor
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener
import android.util.Log
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.dal.remote.data.UserFb

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 *
 * Note: This class is a singleton. The companion object can be found at the bottom of the file
 */
class UserRepository private constructor(
        private var userDao: UserDao,
        private var diskExecutor : Executor,
        private var networkExecutor : Executor
    ) : IUserRepository {

    private val mAuth = FirebaseAuth.getInstance()
    private val remoteUserTable : DatabaseReference
            = FirebaseDatabase.getInstance().reference.child("users")

    //Query
    override fun getUser(id : String): LiveData<User> {
        return userDao.loadUser(id)
    }

    override fun getUsers(): LiveData<List<UserMinimal>> {
        return userDao.loadUsers()
    }

    override fun getUsersByTopic(topic : String): LiveData<List<UserMinimal>> {
        return userDao.loadUsersByTopic(topic)
    }

    //Insert
    override fun registerUser(template: UserTemplate) : LiveData<DalResponse> {
        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        mAuth.createUserWithEmailAndPassword(template.email, template.password)
            .addOnCompleteListener(networkExecutor, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    Log.d("SIGNUP", "createUserWithEmail:success")
                    val user = mAuth.currentUser!!
                    val userFb = UserFb(template)
                    remoteUserTable.child(user.uid).setValue(userFb , { error, _ ->
                        run {
                            if (error == null) {
                                diskExecutor.execute {
                                    var newUser = userFb.toEntity(user.uid)
                                    userDao.insertUserMinimal(newUser.getUserMinimal())
                                    userDao.insertUserDetails(newUser.getUserDetails())
                                    result.postValue(DalResponse(DalResponseStatus.SUCCESS, user.uid))
                                }
                            }
                            else {
                                user.delete()
                                result.postValue(DalResponse(DalResponseStatus.FAIL, null))
                            }
                        }
                    })
                } else {
                    result.postValue(DalResponse(DalResponseStatus.FAIL, null))
                }
            })

        return result
    }

    //Update
    override fun updateUser(user: User) : LiveData<DalResponse> {
        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        val currentUser = mAuth.currentUser
        if(currentUser != null){
            if (currentUser.uid != user.id){
                result.postValue(DalResponse(DalResponseStatus.FAIL, null))
            }
            else {
                networkExecutor.execute {
                    val updateInfo = HashMap<String, Any?>()
                    updateInfo["nickname"] = user.nickname
                    updateInfo["website"] = user.website
                    updateInfo["firstname"] = user.firstName
                    updateInfo["lastname"] = user.lastName
                    updateInfo["age"] = user.age
                    updateInfo["gender"] = user.gender.name

                    remoteUserTable.child(user.id).updateChildren(updateInfo, {
                        error, _ -> run {
                        if (error == null) {
                            diskExecutor.execute {
                                userDao.updateUserMinimal(user.getUserMinimal())
                                userDao.updateUserDetails(user.getUserDetails())
                                result.postValue(DalResponse(DalResponseStatus.SUCCESS, null))
                            }
                        }
                        else {
                            result.postValue(DalResponse(DalResponseStatus.FAIL, null))
                        }
                    }
                    })
                }
            }
        }

        return result
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: UserRepository? = null

        fun getInstance(userDao: UserDao, diskExecutor: Executor, networkExecutor: Executor)
                : UserRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: UserRepository(userDao, diskExecutor, networkExecutor)
            }
        }
    }
}