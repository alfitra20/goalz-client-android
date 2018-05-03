package emse.mobisocial.goalz.util

import emse.mobisocial.goalz.R

/**
 * Created by MobiSocial EMSE Team on 5/3/2018.
 */

private val avatarArray : Array<Int> = arrayOf(
        R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4)

fun getAvatarImageResource(avatarId : Int) : Int{
    try {
        return avatarArray[avatarId]
    }
    catch (e : Exception){
        return avatarArray[0]
    }
}