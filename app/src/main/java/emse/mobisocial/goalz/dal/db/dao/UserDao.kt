package emse.mobisocial.goalz.dal.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserBasic
import emse.mobisocial.goalz.model.UserDetails

/**
 * Created by dtoni on 3/25/2018.
 */
@Dao
abstract class UserDao {
    //QUERY EXISTING USERS
    @Query("SELECT * FROM users")
    abstract fun loadUsers(): LiveData<List<UserBasic>>

    /*@Query("SELECT * FROM users "
            + "WHERE EXISTS (SELECT * FROM recommendations "
            + "JOIN resources ON resources.resource_id = recommendations.resource_id "
            + "WHERE recommendation.user_id = users.user_id AND resources.topic = :topic)")
    abstract fun searchUsersByTopic(topic : String): LiveData<List<UserBasic>>*/

    //TODO: ADD FILTERING CAPABILITIES

    @Query("SELECT * FROM users WHERE user_id = :id")
    abstract fun loadUserBasicById(id : Int): LiveData<UserBasic>

    @Query("SELECT * FROM user_details WHERE user_id = :id")
    abstract fun loadUserDetailsById(id : Int): LiveData<UserDetails>

    //CREATE NEW USERS
    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract fun insertUserBasicInfo(userBasic : UserBasic) : Long

    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract fun insertUserDetailsInfo(userDetails : UserDetails) : Long

    //MODIFY EXISTING USER
    @Update
    abstract fun updateUserBasicInfo(userBasic : UserBasic) : Int

    @Update
    abstract fun updateUserDetailsInfo(userDetails : UserDetails) : Int

    //DELETE USER
    @Delete
    abstract fun deleteUser(userBasic : UserBasic) : Int

    //WARNING: The following methods should be used only inside the repository class
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUserBasicInfo(userBasicList : List<UserBasic>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUserDetailInfo(userBasicList : List<UserDetails>)
}