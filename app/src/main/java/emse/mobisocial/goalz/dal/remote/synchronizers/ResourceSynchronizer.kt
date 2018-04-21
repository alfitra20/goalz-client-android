package emse.mobisocial.goalz.dal.remote.synchronizers

import emse.mobisocial.goalz.dal.db.dao.ResourceDao
import emse.mobisocial.goalz.dal.remote.ISynchronizer
import emse.mobisocial.goalz.model.Resource
import java.util.concurrent.Executor

/**
 * Created by MobiSocial EMSE Team on 4/20/2018.
 */
class ResourceSynchronizer(
        private var resourceDao: ResourceDao,
        private var diskExecutor: Executor) : ISynchronizer<Resource> {

    init {
        diskExecutor.execute { resourceDao.invalidateData() }
    }

    override fun addData(t: Resource) {
        diskExecutor.execute { resourceDao.insertResource(t) }
    }

    override fun updateData(t: Resource) {
        diskExecutor.execute { resourceDao.updateResource(t) }
    }

    override fun deleteData(t: Resource) {
        diskExecutor.execute { resourceDao.deleteResource(t) }
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: ResourceSynchronizer? = null

        fun getInstance(resourceDao: ResourceDao, diskExecutor: Executor) : ResourceSynchronizer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: ResourceSynchronizer(resourceDao, diskExecutor)
            }
        }
    }
}