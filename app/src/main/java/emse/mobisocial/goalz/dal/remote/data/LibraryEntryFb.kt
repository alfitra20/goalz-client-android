package emse.mobisocial.goalz.dal.remote.data

import emse.mobisocial.goalz.dal.remote.FirebaseData
import emse.mobisocial.goalz.model.LibraryEntry

/**
 * Created by MobiSocial EMSE Team on 4/20/2018.
 */
class LibraryEntryFb  constructor() : FirebaseData<LibraryEntry> {

    var user_id : String? = null
    var resource_id : String? = null

    constructor(userId : String, resourceId : String) : this() {
        user_id = userId
        resource_id = resourceId
    }

    override fun toEntity(id: String): LibraryEntry {
        return LibraryEntry(id, user_id!!, resource_id!!)
    }

}