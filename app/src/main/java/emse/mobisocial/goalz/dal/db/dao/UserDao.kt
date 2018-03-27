package emse.mobisocial.goalz.dal.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserDetails

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
@Dao
abstract class UserDao {
    //QUERY EXISTING USERS
    @Query("SELECT * FROM users")
    abstract fun loadUsers(): LiveData<List<User>>

    /*@Query("SELECT * FROM users "
            + "WHERE EXISTS (SELECT * FROM recommendations "
            + "JOIN resources ON resources.resource_id = recommendations.resource_id "
            + "WHERE recommendation.user_id = users.user_id AND resources.topic = :topic)")
    abstract fun searchUsersByTopic(topic : String): LiveData<List<User>>*/

    //TODO: ADD FILTERING CAPABILITIES

    @Query("SELECT * FROM users WHERE user_id = :id")
    abstract fun loadUser(id : Int): LiveData<User>

    @Query("SELECT * FROM users WHERE user_id = :id")
    abstract fun loadUserForDelete(id : Int): User

    @Query("SELECT * FROM user_details WHERE user_id = :id")
    abstract fun loadUserDetails(id : Int): LiveData<UserDetails>

    //CREATE NEW USERS
    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract fun insertUsers(user: User) : Long

    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract fun insertUserDetails(userDetails : UserDetails) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUserList(userList: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUserDetailsList(userDetailList : List<UserDetails>)

    //MODIFY EXISTING USER
    @Update
    abstract fun updateUser(user: User) : Int

    @Update
    abstract fun updateUserDetails(userDetails : UserDetails) : Int

    //DELETE USER
    @Delete
    abstract fun deleteUser(user: User) : Int
}