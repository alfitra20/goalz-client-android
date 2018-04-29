package emse.mobisocial.goalz.ui

/**
 * Created by MobiSocial EMSE Team on 4/28/2018.
 */
interface IDialogResultListener {

    enum class DialogResult {CONFIRM, CANCEL}

    fun callback(tag : String, result : DialogResult, value : Any?)
}