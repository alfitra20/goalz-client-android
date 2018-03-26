package emse.mobisocial.goalz.dal

import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.model.UserBasic
import emse.mobisocial.goalz.model.UserDetails
import emse.mobisocial.goalz.model.UserInfo

/**
 * Created by dtoni on 3/26/2018.
 */
interface IUserRepository {

    fun registerUser(userInfo: UserInfo) : LiveData<Int>

    fun updateUser(userBasic: UserBasic) : LiveData<Boolean>

    fun updateUser(userBasic: UserBasic, userDetails: UserDetails) : LiveData<Boolean>

    fun updateUserDetails(userDetails: UserDetails) : LiveData<Boolean>

    fun deleteUser(userBasic: UserBasic) : LiveData<Boolean>

    fun getUsers(): LiveData<List<UserBasic>>

    fun getUserBasic(id : Int): LiveData<UserBasic>

    fun getUserDetails(id : Int): LiveData<UserDetails>
}