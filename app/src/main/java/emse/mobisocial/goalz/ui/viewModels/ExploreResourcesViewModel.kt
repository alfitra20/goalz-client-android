package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.model.Resource

class ExploreResourcesViewModel(application: Application): AndroidViewModel(application) {

    private val resourceRepository: IResourceRepository = (application as GoalzApp).resourceRepository
    private var resourcesListDb = MutableLiveData<LiveData<List<Resource>>>()
    val resourcesList: LiveData<List<Resource>> = Transformations.switchMap(resourcesListDb) { it }

    init {
        resourcesListDb.postValue(resourceRepository.getResources())
    }

    fun searchResources(searchQuery: String) {
        if (searchQuery == ""){
            resourcesListDb.postValue(resourceRepository.getResources())
        }
        else {
            val formattedQuery = "%$searchQuery%"
            resourcesListDb.postValue(resourceRepository.searchResources(formattedQuery))
        }
    }
}