package emse.mobisocial.goalz.dal

import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserMinimal
import emse.mobisocial.goalz.model.UserDetails
import emse.mobisocial.goalz.model.UserTemplate

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
interface IUserRepository {

    //Query
    fun getUser(id : String): LiveData<User>

    fun getUsers(): LiveData<List<UserMinimal>>

    fun getUsersByTopic(topic : String): LiveData<List<UserMinimal>>

    //Insert
    fun registerUser(template: UserTemplate) : LiveData<DalResponse>

    //Update
    fun updateUser(user: User) : LiveData<DalResponse>
}