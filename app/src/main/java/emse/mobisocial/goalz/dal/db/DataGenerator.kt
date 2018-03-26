package emse.mobisocial.goalz.dal.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey
import emse.mobisocial.goalz.model.Gender
import emse.mobisocial.goalz.model.UserBasic
import emse.mobisocial.goalz.model.UserDetails
import java.util.*

/**
 * Created by dtoni on 3/26/2018.
 */

private val users = arrayOf(
    UserBasic(1, "Toniuc", 2.4, null, Date()),
    UserBasic(2, "Hamek", 1.7, "www.react.com", Date()),
    UserBasic(3, "Alfitra", 3.8, "www.music.com", Date()),
    UserBasic(4, "Sabina", 2.1, null, Date())
)

private val userDetails = arrayOf(
    UserDetails(1, "Daniel", "Toniuc",  "dtoniuc@gmail.com", 23, Gender.MALE),
    UserDetails(2, "Chouaib", "Hamek", "randomEmail@oulu.fi", 38, Gender.UNDEFINED),
    UserDetails(3, "Alfitra", "Rahman", "rahman@indonesia.com", 18, Gender.MALE),
    UserDetails(4, "Sabina", "Fataliyeva", "sabina@oulu.fi", 24, Gender.FEMALE)
)

abstract class DataGenerator {

    companion object {
        fun generateUsers() : List<UserBasic> {
            return users.toList()
        }

        fun generateUserDetails() : List<UserDetails> {
            return userDetails.toList()
        }
    }

}