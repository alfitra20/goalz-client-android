package emse.mobisocial.goalz.test.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.model.ResourceTemplate

private val NEW_RESOURCE_INFO = ResourceTemplate(
        1,
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
        resourceListDb.postValue(resourceRepository.getLibraryForUser(idText.toInt()))
    }

    fun deleteResource(id : Int) {
        resourceRepository.deleteResource(id)
    }

    fun createResource() {
        resourceRepository.addResource(NEW_RESOURCE_INFO)
    }
}