package emse.mobisocial.goalz.ui

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.util.IDialogResultListener

/**
 * Created by MobiSocial EMSE Team on 4/28/2018.
 */
class GoalActivityProgressDialog constructor(): DialogFragment() {

    private lateinit var resultListener : IDialogResultListener

    private lateinit var progressSb : SeekBar
    private lateinit var progressTw : TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        resultListener = activity as IDialogResultListener

        val initialProgress = arguments.getInt("progress_value")

        val inflater = activity.getSystemService (Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.activity_goal_progress_dialog, null)

        progressSb = view.findViewById(R.id.activity_goal_progress_dialog_progress_sb)
        progressTw = view.findViewById(R.id.activity_goal_progress_dialog_progress_tw)

        progressSb.progress = initialProgress
        progressTw.text = createProgressText(initialProgress)

        progressSb.setOnSeekBarChangeListener(ProgressSeekBarListener())
        val builder = AlertDialog.Builder(activity)

        builder.setView(view)
                .setPositiveButton( R.string.goal_activity_delete_dialog_positive, { _, _ ->
                    resultListener.callback(tag, IDialogResultListener.DialogResult.CONFIRM, progressSb.progress)
                }).setNegativeButton( R.string.goal_activity_delete_dialog_negative, { _, _ ->
                    resultListener.callback(tag, IDialogResultListener.DialogResult.CANCEL, initialProgress)
                })

        return builder.create()
    }

    private fun createProgressText(progress : Int) : String{
        return progress.toString() + getString(R.string.goal_activity_status_template)
    }

    inner class ProgressSeekBarListener : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(p0: SeekBar?) {
            return
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
            return
        }

        override fun onProgressChanged(view: SeekBar?, progress: Int, isUserAction: Boolean) {
            progressTw.text = createProgressText(progress)
        }

    }
}