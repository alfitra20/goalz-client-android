package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import com.google.firebase.database.MutableData
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.model.Goal

class ExploreGoalsViewModel(application: Application): AndroidViewModel(application) {

    private val goalRepository: IGoalRepository = (application as GoalzApp).goalRepository
    private var goalsListDb = MutableLiveData<LiveData<List<Goal>>>()
    val goalsList: LiveData<List<Goal>> = Transformations.switchMap(goalsListDb) { it }

    init {
        goalsListDb.postValue(goalRepository.getGoals())
    }

    fun searchGoals(searchQuery : String) {
        if (searchQuery == ""){
            goalsListDb.postValue(goalRepository.getGoals())
        }
        else {
            val formattedQuery = "%$searchQuery%"
            goalsListDb.postValue(goalRepository.searchGoals(formattedQuery))
        }
    }
}