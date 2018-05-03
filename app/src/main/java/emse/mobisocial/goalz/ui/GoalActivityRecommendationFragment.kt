package emse.mobisocial.goalz.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.constraint.Guideline
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Recommendation
import emse.mobisocial.goalz.ui.dialogs.GoalProgressDialog
import emse.mobisocial.goalz.ui.dialogs.RecommendationRateDialog
import emse.mobisocial.goalz.ui.resource_library.WebViewActivity


class GoalActivityRecommendationFragment : Fragment() {

    private var isAuthorized: Boolean = false
    private lateinit var recyclerView : RecyclerView
    private var recyclerViewAdapter : RecyclerViewAdapter = RecyclerViewAdapter(ArrayList<Recommendation>())

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_goal_activity_recommendation, container, false)

        val layoutManager = LinearLayoutManager(activity)

        recyclerView = view.findViewById(R.id.recommendation_activity_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = recyclerViewAdapter

        return view
    }

    fun updateContent(newList : List<Recommendation>, isAuth : Boolean) {
        this.isAuthorized = isAuth
        Log.d("TEST","UPDATE STATE " + isAuth.toString())
        recyclerViewAdapter.addItems(newList)
    }

    fun updateState(isAuth : Boolean) {
        Log.d("TEST","UPDATE STATE " + isAuth.toString())
        this.isAuthorized = isAuth
    }

    inner class RecyclerViewAdapter(values : List<Recommendation>) : RecyclerView.Adapter<RecyclerViewAdapter.RecommendationViewHolder>() {
        private var values: List<Recommendation> = values

        fun addItems(newValues: List<Recommendation>) {
            this.values = newValues
            notifyDataSetChanged()
        }

        private fun loadRatingImages(rating : Int, holder: RecommendationViewHolder){
            when (rating) {
                in 1..5 -> {
                    for (pos in 1..rating) { holder.ratingArray[pos - 1].setImageResource(R.drawable.thumbs_up) }
                    for (pos in (rating + 1)..5) { holder.ratingArray[pos - 1].setImageResource(R.drawable.thumbs_up_blank)  }
                }
                0 -> { for (pos in 0..4) { holder.ratingArray[pos].setImageResource(R.drawable.thumbs_up_blank) } }
                else -> {
                    for (pos in rating..-1) { holder.ratingArray[pos * (-1) - 1].setImageResource(R.drawable.thumbs_down) }
                    for (pos in -5..(rating - 1)) { holder.ratingArray[pos * (-1) - 1].setImageResource(R.drawable.thumbs_down_blank) }
                }
            }
        }

        private fun loadUrlImage(imageUrl : String?, holder : RecommendationViewHolder){
            if (imageUrl != null) {
                Glide.with(context).resumeRequests()
                Glide.with(context).load(imageUrl).priority(Priority.IMMEDIATE).crossFade().into(holder.recImage)
            }
            else {
                Glide.with(context).resumeRequests()
                holder.recImage.setImageResource(R.drawable.goal_grey)
            }
        }

        private fun formatTime(time : Int) : String {
            return when {
                time < 0 -> "Unknown"
                time < 60 -> "$time min"
                else -> "${time / 60} hr"
            }
        }

        private fun formatRecommendationNo(recNo : Int) : String {
            return if (recNo == 1) "$recNo review" else "$recNo reviews"
        }

        override fun getItemCount(): Int {
            return values.size
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecommendationViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.recommendation_card, viewGroup, false)
            return RecommendationViewHolder(v)
        }

        override fun onBindViewHolder(holder: RecommendationViewHolder, i: Int) {
            // The data from the goal model is retrieved and bound to the card View here.
            val recommendation = values[i]
            val resource = recommendation.resource[0]

            holder.recTitle.text = recommendation.title
            holder.recTopic.text = resource.topic
            holder.recRatingCount.text = formatRecommendationNo(resource.recommendation_no)
            holder.recTime.text = formatTime(recommendation.reqTime)
            holder.descriptionTw.text = recommendation.description
            holder.linkTw.text = "Link: ${resource.link}"
            loadRatingImages(resource.rating.toInt(), holder)
            loadUrlImage(resource.imageUrl, holder)

            holder.addToCalendarBtn.visibility = View.GONE
            holder.rateBtn.visibility = View.GONE
            holder.linkTw.visibility = View.GONE
            holder.guideline.visibility = View.INVISIBLE
            holder.descriptionTw.maxLines = 2
            holder.expandBtn.setImageResource(R.drawable.ic_expand_more_black_36dp)

            holder.itemView.setOnClickListener {view ->
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.resource_click))
                try {
                    val builder = CustomTabsIntent.Builder()
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(context, Uri.parse(resource.link))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    val intent = Intent(context, WebViewActivity::class.java) // if there is no google chrome browser
                    intent.putExtra("url", resource.link)
                    context.startActivity(intent)
                }
            }

            holder.addToCalendarBtn.setOnClickListener({
                Toast.makeText(activity, getString(R.string.unavailable_function_toast), Toast.LENGTH_LONG).show()
            })
            holder.rateBtn.setOnClickListener({
                val dialogFragment = RecommendationRateDialog()
                val args = Bundle()

                args.putInt("old_rating", recommendation.rating.toInt())
                args.putString("recommendation_id", recommendation.id)
                dialogFragment.arguments = args

                dialogFragment.show(activity.fragmentManager, getString(R.string.goal_activity_rate_dialog_tag))
            })
        }

        inner class RecommendationViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private var isExpanded : Boolean = false

            internal var recTitle: TextView = itemView.findViewById(R.id.recommendation_card_title)
            internal var recTopic: TextView = itemView.findViewById(R.id.recommendation_card_topic)
            internal var recImage: ImageView = itemView.findViewById(R.id.recommendation_card_resource_image)
            internal var expandBtn: ImageButton = itemView.findViewById(R.id.recommendation_card_expand_btn)
            internal var recRatingLv1: ImageView = itemView.findViewById(R.id.recommendation_card_rating_level_1)
            internal var recRatingLv2: ImageView = itemView.findViewById(R.id.recommendation_card_rating_level_2)
            internal var recRatingLv3: ImageView = itemView.findViewById(R.id.recommendation_card_rating_level_3)
            internal var recRatingLv4: ImageView = itemView.findViewById(R.id.recommendation_card_rating_level_4)
            internal var recRatingLv5: ImageView = itemView.findViewById(R.id.recommendation_card_rating_level_5)
            internal var recRatingCount: TextView = itemView.findViewById(R.id.recommendation_card_rating_count)
            internal var recTime: TextView = itemView.findViewById(R.id.recommendation_card_time)
            internal var descriptionTw: TextView = itemView.findViewById(R.id.recommendation_card_description_tw)
            internal var linkTw: TextView = itemView.findViewById(R.id.recommendation_card_link_tw)
            internal var addToCalendarBtn: Button = itemView.findViewById(R.id.recommendation_card_add_to_calendar_btn)
            internal var rateBtn : Button = itemView.findViewById(R.id.recommendation_card_rate_btn)
            internal var guideline : Guideline = itemView.findViewById(R.id.guideline2)

            internal var ratingArray : Array<ImageView> = arrayOf(recRatingLv1, recRatingLv2, recRatingLv3, recRatingLv4, recRatingLv5)

            init {
                descriptionTw.maxLines = 2
                addToCalendarBtn.visibility = View.GONE
                rateBtn.visibility = View.GONE
                linkTw.visibility = View.GONE
                guideline.visibility = View.INVISIBLE
                expandBtn.setImageResource(R.drawable.ic_expand_more_black_36dp)

                expandBtn.setOnClickListener {
                    isExpanded = !isExpanded

                    if (isExpanded) {
                        descriptionTw.maxLines = 200
                        linkTw.visibility = View.VISIBLE
                        guideline.visibility = View.GONE
                        expandBtn.setImageResource(R.drawable.ic_expand_less_black_36dp)

                        Log.d("TEST","BEFORE EXPANDED " + isAuthorized.toString())
                        if(isAuthorized) {
                            Log.d("TEST","EXPANDED " + isAuthorized.toString())
                            addToCalendarBtn.visibility = View.VISIBLE
                            rateBtn.visibility = View.VISIBLE
                        }
                    }
                    else{
                        descriptionTw.maxLines = 2
                        addToCalendarBtn.visibility = View.GONE
                        rateBtn.visibility = View.GONE
                        linkTw.visibility = View.GONE
                        guideline.visibility = View.INVISIBLE
                        expandBtn.setImageResource(R.drawable.ic_expand_more_black_36dp)
                    }
                }
            }
        }
    }
}
