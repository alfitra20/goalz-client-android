package emse.mobisocial.goalz.dal.remote.data

import emse.mobisocial.goalz.dal.remote.FirebaseData
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.model.ResourceTemplate

/**
 * Created by MobiSocial EMSE Team on 4/19/2018.
 */
class ResourceFb constructor() : FirebaseData<Resource> {

    var id : String? = null
    var user_id : String? = null
    var link : String? = null
    var imageUrl : String? = null
    var title : String? = null
    var topic : String? = null
    var rating : Double = 0.0
    var avgReqTime : Int = -1
    var recommendation_no : Int = 0

    constructor(template : ResourceTemplate, imageUrl : String?) : this() {
        user_id = template.user_id
        link = template.link
        title = template.title
        topic = template.topic
        this.imageUrl = imageUrl
    }

    override fun toEntity(id: String): Resource {
        return Resource(id, user_id, link!!, imageUrl, title!!, topic!!, rating, recommendation_no, avgReqTime)
    }
}