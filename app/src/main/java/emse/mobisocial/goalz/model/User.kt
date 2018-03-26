package emse.mobisocial.goalz.model

/**
 * Created by dtoni on 3/25/2018.
 */

private const val NEW_USER_ID = 0
private const val NEW_USER_RATING : Double = 0.0

data class User constructor(
        // This constructor is used by the data layer. DO NOT use it in any upper layers
        var userBasic: UserBasic, var userDetails: UserDetails) {

    constructor( // This constructor should be used by the application logic to create new users
        nickname : String, firstName : String, lastName : String, email : String, age : Int,
        website : String? = null, gender : Gender = Gender.UNDEFINED
    ) : this(UserBasic(NEW_USER_ID, nickname, NEW_USER_RATING, website, null),
            UserDetails(NEW_USER_ID, firstName, lastName, email, age, gender)){
    }
}