package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.model.ResourceTemplate

/**
 * Created by MobiSocial EMSE Team on 4/30/2018.
 */
class CreateResourceViewModel (application: Application) : AndroidViewModel(application){

    private val resourceRepository : IResourceRepository = (application as GoalzApp).resourceRepository

    fun addResource(newResource: ResourceTemplate) : LiveData<DalResponse> {
        return resourceRepository.addResource(newResource)
    }
}