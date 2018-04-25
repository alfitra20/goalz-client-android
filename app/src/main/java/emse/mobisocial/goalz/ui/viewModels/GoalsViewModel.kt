package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.dal.IRecommendationRepository
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.Recommendation

class GoalsViewModel(application: Application): AndroidViewModel(application) {

    private val goalRepository: IGoalRepository = (application as GoalzApp).goalRepository
    private val recommendationRepository: IRecommendationRepository = (application as GoalzApp).recommendationRepository

    private var goalsListDb = MutableLiveData<LiveData<List<Goal>>>()
    private var recommendationsListDb = MutableLiveData<LiveData<List<Recommendation>>>()

    val goalsList: LiveData<List<Goal>> = Transformations.switchMap(goalsListDb) { it }
    val recommendationsList: LiveData<List<Recommendation>> = Transformations.switchMap(recommendationsListDb) { it }

    fun initialize(user_id: String) {
        goalsListDb.postValue(goalRepository.getGoalsForUser(user_id))
        recommendationsListDb.postValue(recommendationRepository.getRecommendationsForUser(user_id))
    }

    fun searchGoalsForUser(searchQuery : String, userId: String) {
        if (searchQuery == ""){
            goalsListDb.postValue(goalRepository.getGoalsForUser(userId))
        }
        else {
            val formattedQuery = "%$searchQuery%"
            goalsListDb.postValue(goalRepository.searchGoalsForUser(formattedQuery, userId))
        }
    }

    fun reset() {
        goalsListDb.postValue(goalRepository.getGoals())
    }
}