package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.IRecommendationRepository
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.model.RecommendationTemplate
import emse.mobisocial.goalz.model.Resource

/**
 * Created by MobiSocial EMSE Team on 4/30/2018.
 */
class CreateRecommendationViewModel (application: Application) : AndroidViewModel(application){

    var userId : String? = null
    var goalId : String? = null

    private val resourceRepository : IResourceRepository = (application as GoalzApp).resourceRepository
    private val recommendationRepository : IRecommendationRepository = (application as GoalzApp).recommendationRepository

    lateinit var resourceLibrary : LiveData<List<Resource>>

    fun initialize(userId : String, goalId : String){
        this.userId = userId
        this.goalId = goalId
        resourceLibrary = resourceRepository.getLibraryForUser(userId)
    }

    fun addRecommendation(template: RecommendationTemplate) : LiveData<DalResponse> {
        return recommendationRepository.addRecommendation(template)
    }
}