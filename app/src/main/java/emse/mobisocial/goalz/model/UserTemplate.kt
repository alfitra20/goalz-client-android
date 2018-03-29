package emse.mobisocial.goalz.model

import emse.mobisocial.goalz.util.Gender

/**
 * Created by dtoni on 3/26/2018.
 *
 * Encapsulate all the information required for UserMinimal creation
 */
data class UserTemplate(
        var nickname : String,
        var password : String,
        var firstname : String,
        var lastname : String,
        var email : String,
        var age : Int,
        var website : String,
        var gender : Gender = Gender.UNDEFINED
)