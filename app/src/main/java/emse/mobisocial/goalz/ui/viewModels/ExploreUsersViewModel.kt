package emse.mobisocial.goalz.ui.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.model.UserMinimal

class ExploreUsersViewModel(application: Application): AndroidViewModel(application) {

    private val userRepository: IUserRepository = (application as GoalzApp).userRepository
    private var usersListDb = MutableLiveData<LiveData<List<UserMinimal>>>()
    val usersList: LiveData<List<UserMinimal>> = Transformations.switchMap(usersListDb) { it }

    init {
        usersListDb.postValue(userRepository.getUsers())
    }

    fun searchUsers(searchQuery : String) {
        if (searchQuery == ""){
            usersListDb.postValue(userRepository.getUsers())
        }
        else {
            val formattedQuery = "%$searchQuery%"
            usersListDb.postValue(userRepository.searchUsers(formattedQuery))
        }
    }
}