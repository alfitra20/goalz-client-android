package emse.mobisocial.goalz.dal.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserMinimal
import emse.mobisocial.goalz.model.UserDetails

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
@Dao
abstract class UserDao {
    @Query("SELECT * FROM users")
    abstract fun loadUsers(): LiveData<List<UserMinimal>>

    @Query("SELECT * FROM users "
            + "WHERE EXISTS (SELECT * FROM recommendations "
            + "JOIN resources ON resources.resource_id = recommendations.resource_id "
            + "WHERE recommendations.user_id = users.user_id AND resources.topic = :topic)")
    abstract fun loadUsersByTopic(topic : String): LiveData<List<UserMinimal>>

    @Query("SELECT * FROM users " +
            "JOIN user_details ON users.user_id = user_details.user_id " +
            "WHERE users.user_id = :id")
    abstract fun loadUser(id : String): LiveData<User>

    @Query("SELECT * FROM users WHERE user_id = :id")
    abstract fun loadUserForDelete(id : String): UserMinimal?

    @Query("SELECT * FROM users WHERE nickname LIKE :formattedQuery OR website LIKE :formattedQuery")
    abstract fun searchUsers(formattedQuery: String): LiveData<List<UserMinimal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUserMinimal(userMinimal: UserMinimal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUserDetails(userDetails: UserDetails)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateUserMinimal(userMinimal: UserMinimal)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateUserDetails(userDetails: UserDetails)

    @Delete
    abstract fun deleteUser(userMinimal: UserMinimal)

    @Query("DELETE FROM users")
    abstract fun invalidateData()
}