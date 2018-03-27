package emse.mobisocial.goalz.dal

import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserDetails
import emse.mobisocial.goalz.model.UserInfo

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
interface IUserRepository {

    //Insert
    fun registerUser(userInfo: UserInfo) : LiveData<Int>

    //Update
    fun updateUser(user: User) : LiveData<Boolean>

    fun updateUser(user: User, userDetails: UserDetails) : LiveData<Boolean>

    fun updateUserDetails(userDetails: UserDetails) : LiveData<Boolean>

    //Delete
    fun deleteUser(user: User) : LiveData<Boolean> // The preferred method

    fun deleteUserById(id : Int) : LiveData<Boolean>

    //Query
    fun getUsers(): LiveData<List<User>>

    fun getUser(id : Int): LiveData<User>

    fun getUserDetails(id : Int): LiveData<UserDetails>
}