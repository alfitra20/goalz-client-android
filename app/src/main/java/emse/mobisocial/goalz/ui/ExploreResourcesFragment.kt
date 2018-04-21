package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.ui.viewModels.ExploreResourcesViewModel
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import android.widget.TextView

class ExploreResourcesFragment : Fragment() {

    private lateinit var model : ExploreResourcesViewModel
    private lateinit var recyclerView: RecyclerView
    // An array can be used to hold the data temporarily. Maybe needed later
    // private lateinit var resourcesList : ArrayList<Resource>
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_explore_resources, container, false)

        // resourcesList = ArrayList<Resource>()

        model = ViewModelProviders.of(this).get(ExploreResourcesViewModel::class.java)

        recyclerView = view.findViewById(R.id.explore_resources_recycler_view) as RecyclerView

        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = RecyclerViewAdapter(ArrayList<Resource>())
        recyclerView.adapter = recyclerViewAdapter

        initializeObservers()

        return view
    }

    private fun initializeObservers() {
        model.resourcesList.observe(this, Observer<List<Resource>> { resources ->
            if (resources != null) {
                recyclerViewAdapter.addItems(resources)
            }
        })
    }

    inner class RecyclerViewAdapter(resourcesParams: ArrayList<Resource>) : RecyclerView.Adapter<RecyclerViewAdapter.ResourceViewHolder>() {
        private var mResources: List<Resource> = resourcesParams

        override fun getItemCount(): Int {
            return mResources.size
        }

        fun addItems(newResourcesList: List<Resource>) {
            this.mResources = newResourcesList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ResourceViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.resource_card, viewGroup, false)
            return ResourceViewHolder(v)
        }

        override fun onBindViewHolder(resourceViewHolder: ResourceViewHolder, i: Int) {
            // The data from the resource model is retrieved and bound to the card View here.
            resourceViewHolder.resourceTitle.text = mResources[i].title
            resourceViewHolder.resourceTopic.text = mResources[i].topic
            resourceViewHolder.resourceUrl.text = mResources[i].link

            var rating = mResources[i].rating.toInt()
            var pictureFill  = 0
            var pictureBlank = 0

            if (rating >= 0) {
                pictureFill = R.drawable.thumbs_up
                pictureBlank = R.drawable.thumbs_up_blank
            }else{
                pictureFill = R.drawable.thumbs_down
                pictureBlank = R.drawable.thumbs_down_blank
            }

            resourceViewHolder.ratingLevel1.setImageResource(pictureBlank)
            resourceViewHolder.ratingLevel2.setImageResource(pictureBlank)
            resourceViewHolder.ratingLevel3.setImageResource(pictureBlank)
            resourceViewHolder.ratingLevel4.setImageResource(pictureBlank)
            resourceViewHolder.ratingLevel5.setImageResource(pictureBlank)
            if(rating>=1) {
                resourceViewHolder.ratingLevel1.setImageResource(pictureFill)
            }
            if(rating>=2) {
                resourceViewHolder.ratingLevel2.setImageResource(pictureFill)
            }
            if(rating>=3){
                resourceViewHolder.ratingLevel3.setImageResource(pictureFill)
            }
            if(rating>=4){
                resourceViewHolder.ratingLevel4.setImageResource(pictureFill)
            }
            if(rating>4){
                resourceViewHolder.ratingLevel5.setImageResource(pictureFill)
            }

        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
            super.onAttachedToRecyclerView(recyclerView)
        }

        inner class ResourceViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var resourceCard: CardView
            internal var resourceTitle: TextView
            internal var resourceTopic: TextView
            internal var resourceUrl: TextView
            internal var ratingLevel1: ImageView
            internal var ratingLevel2: ImageView
            internal var ratingLevel3: ImageView
            internal var ratingLevel4: ImageView
            internal var ratingLevel5: ImageView


            init {
                resourceCard = itemView.findViewById<View>(R.id.resource_card_view) as CardView
                resourceTitle = itemView.findViewById<View>(R.id.resource_title) as TextView
                resourceTopic = itemView.findViewById<View>(R.id.resource_topic) as TextView
                resourceUrl = itemView.findViewById<View>(R.id.resource_url) as TextView
                ratingLevel1 = itemView.findViewById<View>(R.id.resource_rating_level_1) as ImageView
                ratingLevel2 = itemView.findViewById<View>(R.id.resource_rating_level_2) as ImageView
                ratingLevel3 = itemView.findViewById<View>(R.id.resource_rating_level_3) as ImageView
                ratingLevel4 = itemView.findViewById<View>(R.id.resource_rating_level_4) as ImageView
                ratingLevel5 = itemView.findViewById<View>(R.id.resource_rating_level_5) as ImageView



            }
        }

    }
}