package emse.mobisocial.goalz.test.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.model.UserMinimal
import emse.mobisocial.goalz.model.*
import emse.mobisocial.goalz.util.Gender

private val NEW_USER_TEMPLATE: UserTemplate =
        UserTemplate(
                "newUser",
                "password",
                "Last",
                "Addition",
                "android@google.com",
                10,
                "www.google.com")

private const val UPDATED_USER_WEBSITE = "random.org"
private const val UPDATED_USER_FIRSTNAME = "John"
private const val UPDATED_USER_LASTNAME = "Smith"
private const val UPDATED_USER_EMAIL = "new_email@random.org"
private const val UPDATED_USER_AGE = 82
private const val UPDATED_USER_GENDER = "MALE"

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 *
 * NOTE: this is a view model used to test the DAL. Create more specific view models
 * for the actual views
 */
class UserTestViewModel (application: Application) : AndroidViewModel(application){

    private val userRepository : IUserRepository = (application as GoalzApp).userRepository

    private var selectedUserId: MutableLiveData<String> = MutableLiveData<String>()
    private val userListDb: MutableLiveData<LiveData<List<UserMinimal>>> = MutableLiveData<LiveData<List<UserMinimal>>>()
    var selectedUser: LiveData<User> = Transformations.switchMap(selectedUserId){
        userRepository.getUser(it)
    }
    val userList: LiveData<List<UserMinimal>> = Transformations.switchMap(userListDb){ it }

    init {
        userListDb.postValue(userRepository.getUsers())
    }

    fun getDetails(id: String) {
        selectedUserId.postValue(id)
    }

    fun updateSelectedUser() {
        var user = selectedUser.value
        if (user != null){
            user.website = UPDATED_USER_WEBSITE
            user.firstName = UPDATED_USER_FIRSTNAME
            user.lastName = UPDATED_USER_LASTNAME
            user.age = UPDATED_USER_AGE
            user.gender = Gender.valueOf(UPDATED_USER_GENDER)

            userRepository.updateUser(user)
        }
    }

    fun registerUser() : LiveData<DalResponse> {
        return userRepository.registerUser(NEW_USER_TEMPLATE)
    }

    fun searchByTopic(topic : String) {
        if (topic == ""){
            userListDb.postValue(userRepository.getUsers())
        } else{
            userListDb.postValue(userRepository.getUsersByTopic(topic))
        }
    }
}