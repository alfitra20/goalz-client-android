package emse.mobisocial.goalz.model

import java.util.*

/**
 * Created by dtoni on 3/25/2018.
 */
data class GoalDetails(var goalId : Int, var status : Int = 0, var deadline : Date? = null)