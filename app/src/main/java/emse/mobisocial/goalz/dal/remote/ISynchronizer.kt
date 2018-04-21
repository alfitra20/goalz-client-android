package emse.mobisocial.goalz.dal.remote

/**
 * Created by MobiSocial EMSE Team on 4/19/2018.
 */
interface ISynchronizer<T> {

    fun addData(t: T)

    fun updateData(t: T)

    fun deleteData(t: T)
}