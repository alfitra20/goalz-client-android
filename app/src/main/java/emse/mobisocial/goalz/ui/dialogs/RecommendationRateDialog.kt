package emse.mobisocial.goalz.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.util.IDialogResultListener

/**
 * Created by MobiSocial EMSE Team on 5/3/2018.
 */
class RecommendationRateDialog constructor(): DialogFragment() {

    data class Load(val id : String, val rating : Int)

    private lateinit var resultListener : IDialogResultListener

    private lateinit var progressSb : SeekBar
    private lateinit var progressTw : TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        resultListener = activity as IDialogResultListener

        val initialProgress = arguments.getInt("old_rating") + 5
        val recommendationId = arguments.getString("recommendation_id")

        val inflater = activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.activity_goal_rate_rec_dialog, null)

        progressSb = view.findViewById(R.id.activity_goal_rate_rec_dialog_progress_sb)
        progressTw = view.findViewById(R.id.activity_goal_rate_rec_dialog_progress_tw)

        progressSb.progress = initialProgress
        progressTw.text = (initialProgress - 5).toString()

        progressSb.setOnSeekBarChangeListener(ProgressSeekBarListener())
        val builder = AlertDialog.Builder(activity)

        builder.setView(view)
                .setPositiveButton( R.string.goal_activity_delete_dialog_positive, { _, _ ->
                    resultListener.callback(tag, IDialogResultListener.DialogResult.CONFIRM,
                            Load(recommendationId, progressSb.progress - 5))
                }).setNegativeButton( R.string.goal_activity_delete_dialog_negative, { _, _ ->
            resultListener.callback(tag, IDialogResultListener.DialogResult.CANCEL,
                    Load(recommendationId, initialProgress - 5))
        })

        return builder.create()
    }

    inner class ProgressSeekBarListener : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(p0: SeekBar?) {
            return
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
            return
        }

        override fun onProgressChanged(view: SeekBar?, progress: Int, isUserAction: Boolean) {
            progressTw.text = (progress - 5).toString()
        }

    }
}