package emse.mobisocial.goalz.model

/**
 * Created by dtoni on 3/26/2018.
 */
data class UserInfo(
        var nickname : String,
        var password : String,
        var firstname : String,
        var lastname : String,
        var email : String,
        var age : Int,
        var website : String,
        var gender : Gender = Gender.UNDEFINED
)