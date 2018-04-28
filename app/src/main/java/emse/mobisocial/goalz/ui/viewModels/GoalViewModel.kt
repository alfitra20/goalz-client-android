package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IGoalRepository

/**
 * Created by MobiSocial EMSE Team on 4/27/2018.
 */
class GoalViewModel(application: Application, val goalId : String): AndroidViewModel(application) {

    enum class State { UNAUTH, AUTH_UNAUTHORIZED, AUTH_AUTHORIZE}

    private val goalRepository: IGoalRepository = (application as GoalzApp).goalRepository

    val goal = goalRepository.getGoal(goalId)
    val subgoals = goalRepository.getSubgoals(goalId)

    val state : MutableLiveData<State> = MutableLiveData<State>()

    init {
        state.postValue(State.UNAUTH)
    }

    fun changeState(newState : State){
        state.postValue(newState)
    }
}