package emse.mobisocial.goalz.test.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import emse.mobisocial.goalz.GoalzApp
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.model.*

private val NEW_USER_INFO : UserInfo =
        UserInfo(
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

    private var selectedUser : MutableLiveData<User> = MutableLiveData<User>()
    val userList : LiveData<List<User>> = userRepository.getUsers()
    var selectedUserDetails : LiveData<UserDetails> = Transformations.switchMap(selectedUser){
        userRepository.getUserDetails(it.id)
    }

    fun getDetails(user: User) {
        selectedUser.postValue(user)
    }

    fun deleteSelectedUser() {
        val user = selectedUser.value
        if (user != null) {
            userRepository.deleteUser(user)
        }
    }

    fun updateSelectedUser() {
        val user = selectedUser.value
        val userDetails = selectedUserDetails.value
        if (user != null && userDetails != null){
            user.website = UPDATED_USER_WEBSITE
            userDetails.firstName = UPDATED_USER_FIRSTNAME
            userDetails.lastName = UPDATED_USER_LASTNAME
            userDetails.email = UPDATED_USER_EMAIL
            userDetails.age = UPDATED_USER_AGE
            userDetails.gender = Gender.valueOf(UPDATED_USER_GENDER)

            userRepository.updateUser(user, userDetails)
        }
    }

    fun registerUser() {
        userRepository.registerUser(NEW_USER_INFO)
    }
}