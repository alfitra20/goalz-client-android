package emse.mobisocial.goalz.ui

import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import emse.mobisocial.goalz.R

/**
 * Created by MobiSocial EMSE Team on 4/28/2018.
 */
class GoalActivityDeleteDialog constructor(): DialogFragment() {

    private lateinit var resultListener : IDialogResultListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        resultListener = activity as IDialogResultListener

        val builder = AlertDialog.Builder(activity)

        builder.setMessage(R.string.goal_activity_delete_dialog_messages)
                .setPositiveButton(R.string.goal_activity_delete_dialog_positive, { _, _ ->
                    resultListener.callback(tag, IDialogResultListener.DialogResult.CONFIRM, null)
                }).setNegativeButton(R.string.goal_activity_delete_dialog_negative, { _, id ->
                    resultListener.callback(tag, IDialogResultListener.DialogResult.CANCEL, null)
                })
        return builder.create()
    }

}