package emse.mobisocial.goalz.test.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.model.ResourceTemplate

private val NEW_RESOURCE_INFO = ResourceTemplate(
        "to2mdRC0eXUvPRVTHL1QJk57Aro2",
        "www.link.org",
        "new",
        "New resource"
)

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
class ResourceTestViewModel (application: Application) : AndroidViewModel(application){

    private val resourceRepository : IResourceRepository = (application as GoalzApp).resourceRepository
    private var resourceListDb: MutableLiveData<LiveData<List<Resource>>> = MutableLiveData<LiveData<List<Resource>>>()
    init {
        resourceListDb.postValue(resourceRepository.getResources())
    }

    val resourceList: LiveData<List<Resource>> = Transformations.switchMap(resourceListDb){ it }

    fun applyByTopicFilter(topic : String){
        if (topic == ""){
            resourceListDb.postValue(resourceRepository.getResources())
        }
        else {
            resourceListDb.postValue(resourceRepository.getResourcesByTopic(topic))
        }
    }

    fun applyByUserFilter(idText : String){
        resourceListDb.postValue(resourceRepository.getLibraryForUser(idText))
    }

    fun deleteResource(id : String) : LiveData<DalResponse> {
        return resourceRepository.deleteResource(id)
    }

    fun addResourceToLibrary(userId : String, resourceId : String) : LiveData<DalResponse> {
        return resourceRepository.addResourceToLibrary(userId, resourceId)
    }

    fun deleteResourceToLibrary(userId : String, resourceId : String) : LiveData<DalResponse> {
        return resourceRepository.deleteResourceFromLibrary(userId, resourceId)
    }

    fun createResource() : LiveData<DalResponse> {
        return resourceRepository.addResource(NEW_RESOURCE_INFO)
    }
}