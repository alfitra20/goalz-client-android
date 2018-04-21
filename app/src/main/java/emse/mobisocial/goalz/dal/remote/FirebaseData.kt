package emse.mobisocial.goalz.dal.remote

/**
 * Created by MobiSocial EMSE Team on 4/19/2018.
 */
interface FirebaseData<T> {
    fun toEntity(id : String) : T
}