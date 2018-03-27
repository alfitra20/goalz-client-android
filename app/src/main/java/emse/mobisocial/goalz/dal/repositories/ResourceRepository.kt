package emse.mobisocial.goalz.dal.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.dal.db.dao.ResourceDao
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.model.ResourceInfo
import java.util.concurrent.Executor

private const val NEW_RESOURCE_ID = 0
private const val NEW_RESOURCE_RATING : Double = 0.0
private const val NEW_AVG_REQ_TIME = -1

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 *
 * Note: This class is a singleton. The companion object can be found at the bottom of the file
 */
class ResourceRepository(
        private var executor : Executor,
        private var resourceDao: ResourceDao)
    : IResourceRepository {

    //Insert
    override fun addResource(info: ResourceInfo) : LiveData<Int> {
        var result = MutableLiveData<Int>()
        executor.execute {
            val resource = Resource(
                    NEW_RESOURCE_ID, info.link, info.title, info.topic,
                    NEW_RESOURCE_RATING, NEW_AVG_REQ_TIME)

            val id = resourceDao.insertResource(resource).toInt()
            result.postValue(id)
        }

        return result
    }

    //Delete
    fun deleteResource(resource: Resource): LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            resourceDao.deleteResource(resource)
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    override fun deleteResourceById(id : Int): LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        executor.execute {
            var resource = resourceDao.loadResourceForDelete(id)
            if (resource != null) {
                resourceDao.deleteResource(resource)
            }
            result.postValue(true) //TODO: This should be changed based on success/fail logic
        }

        return result
    }

    //Query
    override fun getResource(id : Int): LiveData<Resource> {
        return resourceDao.loadResource(id)
    }

    override fun getResources(): LiveData<List<Resource>> {
        return resourceDao.loadResources()
    }

    override fun getResourcesByTopic(topic: String): LiveData<List<Resource>> {
        return resourceDao.loadResourcesByTopic(topic)
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: ResourceRepository? = null

        fun getInstance(executor: Executor, resourceDao: ResourceDao): ResourceRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: ResourceRepository(executor, resourceDao)
            }
        }
    }
}