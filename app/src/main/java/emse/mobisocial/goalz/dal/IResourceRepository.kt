package emse.mobisocial.goalz.dal

import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.model.LibraryEntry
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.model.ResourceTemplate

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
interface IResourceRepository {

    //Query
    fun getResource(id : String): LiveData<Resource>

    fun getResources(): LiveData<List<Resource>>

    fun getResourcesByTopic(topic : String): LiveData<List<Resource>>

    fun getLibraryForUser(userId : String): LiveData<List<Resource>>

    //Insert
    fun addResource(template: ResourceTemplate) : LiveData<DalResponse>

    fun addResourceToLibrary(user_id : String, resource_id : String): LiveData<DalResponse>

    fun deleteResourceFromLibrary(user_id : String, resource_id : String): LiveData<DalResponse>

    /* Delete
     *
     * WARNING: This method is created to make testing and debugging easier. Do not implement
     *          delete functionality for resources since it is not supported by the API. Once
     *          created a resource stays created and cannot be removed/modified. This is a
     *          limitation of the current API and we can say that it will be fixed in further
     *          versions when Admin users will be added
     *
     *TODO: remove this method in the final version
     */
    fun deleteResource(id : String) : LiveData<DalResponse>
}