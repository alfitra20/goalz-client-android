package emse.mobisocial.goalz.dal.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import emse.mobisocial.goalz.model.Resource

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
@Dao
abstract class ResourceDao {
    @Query("SELECT * FROM resources")
    abstract fun loadResources() : LiveData<List<Resource>>

    @Query("SELECT * FROM resources WHERE topic = :topic")
    abstract fun loadResourcesByTopic(topic : String) : LiveData<List<Resource>>

    @Query("SELECT * FROM resources WHERE resource_id = :id")
    abstract fun loadResource(id : Int): LiveData<Resource>

    @Query("SELECT * FROM resources WHERE resource_id = :id")
    abstract fun loadResourceForDelete(id : Int): Resource?

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT resources.resource_id, resources.user_id, link, resources.title, topic, resources.rating, avg_req_time FROM resources " +
            "JOIN user_library ON resources.resource_id = user_library.resource_id " +
            "WHERE user_library.user_id = :userId")
    abstract fun loadLibraryForUser(userId : Int) : LiveData<List<Resource>>

    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract fun insertResource(resource: Resource) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertResourceList(userList: List<Resource>)


    @Delete
    abstract fun deleteResource(resource: Resource) : Int


    @Update
    abstract fun updateResource(resource: Resource) : Int
}