package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.model.UserMinimal

class ExploreUsersViewModel(application: Application): AndroidViewModel(application) {

    private val userRepository: IUserRepository = (application as GoalzApp).userRepository
    private var usersListDb: LiveData<List<UserMinimal>> = userRepository.getUsers()
    val usersList: LiveData<List<UserMinimal>> = Transformations.map(usersListDb) { it }
}