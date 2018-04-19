package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.model.Goal

class ExploreGoalsViewModel(application: Application): AndroidViewModel(application) {

    private val goalRepository: IGoalRepository = (application as GoalzApp).goalRepository
    private var goalsListDb: LiveData<List<Goal>> = goalRepository.getGoals()
    val goalsList: LiveData<List<Goal>> = Transformations.map(goalsListDb) { it }
}