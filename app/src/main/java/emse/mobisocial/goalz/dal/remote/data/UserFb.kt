package emse.mobisocial.goalz.dal.remote.data

import emse.mobisocial.goalz.dal.remote.FirebaseData
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserTemplate
import emse.mobisocial.goalz.util.Gender
import java.util.*

/**
 * Created by MobiSocial EMSE Team on 4/20/2018.
 */
class UserFb constructor() : FirebaseData<User> {

    var nickname : String? = null
    var rating : Double = 0.0
    var website : String? = null
    var registrationDate : Long = Date().time / 1000
    var firstname : String? = null
    var lastname : String? = null
    var email : String? = null
    var age : Int? = null
    var gender : String? = null

    constructor(template: UserTemplate) : this() {
        nickname = template.nickname
        website = template.website
        firstname = template.firstname
        lastname = template.lastname
        email = template.email
        age = template.age
        gender = template.gender?.name
    }

    override fun toEntity(id: String): User {
        return User(id, nickname!!, rating, website, Date(registrationDate*1000), firstname!!
                , lastname!!, email!!, age!!, Gender.valueOf(gender!!))
    }

}