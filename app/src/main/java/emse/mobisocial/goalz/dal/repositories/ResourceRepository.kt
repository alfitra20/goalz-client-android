package emse.mobisocial.goalz.dal.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.dal.db.dao.ResourceDao
import emse.mobisocial.goalz.dal.db.dao.LibraryDao
import emse.mobisocial.goalz.dal.remote.data.LibraryEntryFb
import emse.mobisocial.goalz.dal.remote.data.ResourceFb
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.model.ResourceTemplate
import emse.mobisocial.goalz.util.ImageExtractor
import java.util.concurrent.Executor

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 *
 * Note: This class is a singleton. The companion object can be found at the bottom of the file
 */
class ResourceRepository(
        private var resourceDao: ResourceDao,
        private var libraryDao: LibraryDao,
        private var diskExecutor : Executor,
        private var networkExecutor : Executor)
    : IResourceRepository {

    private val remoteResourceTable : DatabaseReference
            = FirebaseDatabase.getInstance().reference.child("resources")
    private val remoteLibraryTable : DatabaseReference
            = FirebaseDatabase.getInstance().reference.child("user_library")

    //Query
    override fun getResource(id : String): LiveData<Resource> {
        return resourceDao.loadResource(id)
    }

    override fun getResources(): LiveData<List<Resource>> {
        return resourceDao.loadResources()
    }

    override fun searchResources(formattedQuery: String): LiveData<List<Resource>> {
        return resourceDao.searchResources(formattedQuery)
    }

    override fun getResourcesByTopic(topic: String): LiveData<List<Resource>> {
        return resourceDao.loadResourcesByTopic(topic)
    }

    override fun getLibraryForUser(userId: String): LiveData<List<Resource>> {
        return resourceDao.loadLibraryForUser(userId)
    }

    //Insert
    override fun addResource(template: ResourceTemplate) : LiveData<DalResponse> {
        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        networkExecutor.execute {
            val newId = remoteResourceTable.push().key
            var imageUrl : String? = null
            try {
                imageUrl = ImageExtractor.extractImageUrl(template.link)
            } catch (e : Exception){
                //The extraction of image failed. We just go on with null imageUrl
            }

            val resourceFb = ResourceFb(template, imageUrl)

            remoteResourceTable.child(newId).setValue(resourceFb , {
                error, _ -> run {
                    if (error == null) {
                        diskExecutor.execute {
                            var newResource = resourceFb.toEntity(newId)
                            resourceDao.insertResource(newResource)
                            result.postValue(DalResponse(DalResponseStatus.SUCCESS, newId))
                        }
                    }
                    else {
                        result.postValue(DalResponse(DalResponseStatus.FAIL, null))
                    }
                }
            })
        }
        return result
    }

    override fun addResourceToLibrary(user_id : String, resource_id : String)
            : LiveData<DalResponse> {

        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        networkExecutor.execute {
            val newId = remoteLibraryTable.push().key
            val libraryEntryFb = LibraryEntryFb(user_id, resource_id)
            remoteLibraryTable.child(newId).setValue(libraryEntryFb , {
                error, _ -> run {
                if (error == null) {
                    diskExecutor.execute {
                        var newEntry = libraryEntryFb.toEntity(newId)
                        libraryDao.insertLibraryEntry(newEntry)
                        result.postValue(DalResponse(DalResponseStatus.SUCCESS, newId))
                    }
                }
                else {
                    result.postValue(DalResponse(DalResponseStatus.FAIL, null))
                }
            }
            })
        }
        return result
    }

    override fun deleteResourceFromLibrary(user_id : String, resource_id : String)
            : LiveData<DalResponse> {

        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        diskExecutor.execute {
            var entry = libraryDao.loadLibraryEntryForDelete(user_id, resource_id)
            if (entry == null)
                result.postValue(DalResponse(DalResponseStatus.SUCCESS, null))
            else {
                networkExecutor.execute {
                    remoteLibraryTable.child(entry.id).removeValue({ error, _ ->
                        run {
                            if (error == null) {
                                diskExecutor.execute {
                                    libraryDao.deleteLibraryEntry(entry)
                                    result.postValue(DalResponse(DalResponseStatus.SUCCESS, null))
                                }
                            } else {
                                result.postValue(DalResponse(DalResponseStatus.FAIL, null))
                            }
                        }
                    })
                }
            }
        }

        return result
    }

    //Delete
    override fun deleteResource(id : String): LiveData<DalResponse> {

        var result = MutableLiveData<DalResponse>()
        result.postValue(DalResponse(DalResponseStatus.INPROGRESS, null))

        if(id == "")
            result.postValue(DalResponse(DalResponseStatus.SUCCESS, null))
        else {
            networkExecutor.execute {
                remoteResourceTable.child(id).removeValue({
                    error, _ -> run {
                    if (error == null) {
                        diskExecutor.execute {
                            var resource = resourceDao.loadResourceForDelete(id)
                            if (resource!= null) {
                                resourceDao.deleteResource(resource)
                            }
                            result.postValue(DalResponse(DalResponseStatus.SUCCESS, null))
                        }
                    }
                    else {
                        result.postValue(DalResponse(DalResponseStatus.FAIL, null))
                    }
                }
                })
            }
        }

        return result
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: ResourceRepository? = null

        fun getInstance(resourceDao: ResourceDao, libraryDao: LibraryDao,
                        diskExecutor: Executor, networkExecutor: Executor)
                : ResourceRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: ResourceRepository(
                                resourceDao, libraryDao, diskExecutor, networkExecutor)
            }
        }
    }
}