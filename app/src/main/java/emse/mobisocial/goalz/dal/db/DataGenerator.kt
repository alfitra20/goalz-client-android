package emse.mobisocial.goalz.dal.db

import emse.mobisocial.goalz.model.Gender
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserDetails
import java.util.*

private val users = arrayOf(
    User(1, "Toniuc", 2.4, null, Date()),
    User(2, "Hamek", 1.7, "www.react.com", Date()),
    User(3, "Alfitra", 3.8, "www.music.com", Date()),
    User(4, "Sabina", 2.1, null, Date())
)

private val userDetails = arrayOf(
    UserDetails(1, "Daniel", "Toniuc",  "dtoniuc@gmail.com", 23, Gender.MALE),
    UserDetails(2, "Chouaib", "Hamek", "randomEmail@oulu.fi", 38, Gender.UNDEFINED),
    UserDetails(3, "Alfitra", "Rahman", "rahman@indonesia.com", 18, Gender.MALE),
    UserDetails(4, "Sabina", "Fataliyeva", "sabina@oulu.fi", 24, Gender.FEMALE)
)

private val resources = arrayOf(
        Resource(1, "www.resource1.com", "First Resource", "random", 3.4, 50),
        Resource(2, "www.google.com", "Dummy resource", "search", 0.2, 10),
        Resource(3, "www.stackoverflow.com", "Profi", "search", 1.5, 30),
        Resource(4, "www.scopus.com", "Uni Database", "random", 2.4, 110)
)

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
abstract class DataGenerator {

    companion object {
        fun generateUsers() : List<User> {
            return users.toList()
        }

        fun generateUserDetails() : List<UserDetails> {
            return userDetails.toList()
        }

        fun generateResources() : List<Resource> {
            return resources.toList()
        }
    }

}