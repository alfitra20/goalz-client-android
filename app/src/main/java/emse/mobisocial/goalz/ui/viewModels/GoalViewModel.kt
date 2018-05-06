package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.dal.IRecommendationRepository

/**
 * Created by MobiSocial EMSE Team on 4/27/2018.
 */
class GoalViewModel(application: Application, val goalId : String): AndroidViewModel(application) {

    enum class State { UNAUTH, AUTH_UNAUTHORIZED, AUTH_AUTHORIZE}

    private val goalRepository: IGoalRepository = (application as GoalzApp).goalRepository
    private val recommendationRepository: IRecommendationRepository = (application as GoalzApp).recommendationRepository

    val goal = goalRepository.getGoal(goalId)
    val subgoals = goalRepository.getSubgoals(goalId)
    val recommendations = recommendationRepository.getRecommendationsForGoal(goalId)

    val state : MutableLiveData<State> = MutableLiveData<State>()

    init {
        state.postValue(State.UNAUTH)
    }

    fun changeState(newState : State){
        state.postValue(newState)
    }

    fun deleteGoal() : LiveData<DalResponse> {
        return goalRepository.deleteGoal(goalId)
    }

    fun updateProgress(newStatus : Int) : LiveData<DalResponse>? {
        val updateState = goal.value ?: return null
        updateState.status = newStatus

        return goalRepository.updateGoal(updateState)
    }

    fun rateRecommendation(recommendationId : String, rating : Int)  : LiveData<DalResponse> {
        return recommendationRepository.rateRecommendation(recommendationId, rating.toDouble())
    }
}