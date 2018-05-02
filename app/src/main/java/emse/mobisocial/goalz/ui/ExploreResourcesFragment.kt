package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.CardView

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.ui.viewModels.ExploreResourcesViewModel
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.google.firebase.auth.FirebaseAuth
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup
import com.nex3z.togglebuttongroup.button.LabelToggle
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.model.DEFAULT_RESOURCE_AVG_TIME
import emse.mobisocial.goalz.ui.resource_library.WebViewActivity
import kotlinx.android.synthetic.main.activity_edit_goal.*

class ExploreResourcesFragment : Fragment() {

    enum class Filter {NONE, TOPIC, RATING, TIME}

    private lateinit var model : ExploreResourcesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    // filter variables and views
    private var filterOpen: Boolean = false
    private lateinit var filterViewLayout : LinearLayout
    private lateinit var filterView: MultiSelectToggleGroup
    private lateinit var topicFilter: LabelToggle
    private lateinit var ratingFilter: LabelToggle
    private lateinit var timeFilter: LabelToggle
    private var selectedFilter = Filter.NONE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_explore_resources, container, false)
        setHasOptionsMenu(true)

        model = ViewModelProviders.of(this).get(ExploreResourcesViewModel::class.java)
        model.userId = FirebaseAuth.getInstance().currentUser?.uid

        // Initialize data
        filterView = view.findViewById(R.id.resources_filters) as MultiSelectToggleGroup
        filterViewLayout = view.findViewById(R.id.resources_filters_layout) as LinearLayout
        recyclerView = view.findViewById(R.id.explore_resources_recycler_view) as RecyclerView
        topicFilter = view.findViewById(R.id.order_resources_topic)
        ratingFilter = view.findViewById(R.id.order_resources_rating)
        timeFilter = view.findViewById(R.id.order_resources_time)
        topicFilter.setTextColor(activity.resources.getColor(R.color.colorPrimary))
        ratingFilter.setTextColor(activity.resources.getColor(R.color.colorPrimary))
        timeFilter.setTextColor(activity.resources.getColor(R.color.colorPrimary))
        topicFilter.markerColor = activity.resources.getColor(R.color.colorSecondary)
        ratingFilter.markerColor = activity.resources.getColor(R.color.colorSecondary)
        timeFilter.markerColor = activity.resources.getColor(R.color.colorSecondary)

        setupRecyclerView()

        initializeObservers()
        initializeListeners()

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = RecyclerViewAdapter(ArrayList<Resource>())
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun initializeObservers() {
        model.resourcesList.observe(this, Observer<List<Resource>> { resources ->
            if (resources != null) {
                recyclerViewAdapter.addItems(resources, selectedFilter)
            }
        })
    }

    private fun initializeListeners() {
        filterView.setOnCheckedChangeListener { _, checkedId, isChecked ->
            if (isChecked){
                when(checkedId) {
                    topicFilter.id -> {
                        if(timeFilter.isChecked) {timeFilter.isChecked = false}
                        if(ratingFilter.isChecked) {ratingFilter.isChecked = false}
                        selectedFilter = Filter.TOPIC
                    }
                    ratingFilter.id -> {
                        if(topicFilter.isChecked) {topicFilter.isChecked = false}
                        if(timeFilter.isChecked) {timeFilter.isChecked = false}
                        selectedFilter = Filter.RATING
                    }
                    timeFilter.id -> {
                        if(topicFilter.isChecked) {topicFilter.isChecked = false}
                        if(ratingFilter.isChecked) {ratingFilter.isChecked = false}
                        selectedFilter = Filter.TIME
                    }
                }
                recyclerViewAdapter.sortRecyclerView(selectedFilter)
            } else if (!isChecked && !topicFilter.isChecked && !topicFilter.isChecked && !timeFilter.isChecked) {
                selectedFilter = Filter.NONE
                recyclerViewAdapter.sortRecyclerView(selectedFilter)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.menu_explore, menu)

        // Setup the behaviour of the search menu item
        val searchItem = menu!!.findItem(R.id.exploreSearch)
        searchView = searchItem.actionView as SearchView
        searchView.setIconified(false)
        searchView.setOnCloseListener(object: SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                searchItem.collapseActionView()
                return true
            }
        })
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(searchQuery: String?): Boolean {
                model.searchResources(searchQuery!!)
                return true
            }

            override fun onQueryTextChange(searchQuery: String?): Boolean {
                model.searchResources(searchQuery!!)
                return true
            }
        })

        // Setup the behaviour of the filter menu item
        val filterItem = menu!!.findItem(R.id.exploreFilter)
        val r = context.resources
        val topMarginPx = r.getDimensionPixelSize(R.dimen.explore_fragment_top_margin)
        val params = recyclerView.getLayoutParams() as ViewGroup.MarginLayoutParams
        filterItem.setOnMenuItemClickListener(object: MenuItem.OnMenuItemClickListener {
            override fun onMenuItemClick(p0: MenuItem?): Boolean {
                if (!filterOpen) {
                    filterViewLayout.visibility = View.VISIBLE
                    params.setMargins(0, 0, 0, 0)
                    recyclerView.setLayoutParams(params)
                } else {
                    filterViewLayout.visibility = View.GONE
                    params.setMargins(0, topMarginPx, 0, 0)
                    recyclerView.setLayoutParams(params)
                }
                filterOpen = !filterOpen
                return true
            }
        })

        super.onCreateOptionsMenu(menu,inflater)
    }

    inner class RecyclerViewAdapter(resourcesParams: ArrayList<Resource>) : RecyclerView.Adapter<RecyclerViewAdapter.ResourceViewHolder>() {
        private var mResources: List<Resource> = resourcesParams
        private var unsortedList: List<Resource> = resourcesParams

        fun addItems(newResourcesList: List<Resource>, filter: Filter) {
            this.unsortedList = newResourcesList
            this.mResources = sortItems(filter)
            notifyDataSetChanged()
        }

        fun sortRecyclerView(filter: Filter){
            mResources = sortItems(filter)
            notifyDataSetChanged()
        }

        private fun sortItems(filter: Filter) : List<Resource> {
            val newList = ArrayList<Resource>(unsortedList)
            when (filter) {
                Filter.TIME -> newList.sortBy { resource -> resource.avgReqTime }
                Filter.TOPIC -> newList.sortBy { resource -> resource.topic }
                Filter.RATING -> newList.sortBy { resource -> -resource.rating }
            }
            return newList
        }

        private fun formatAvgTime(time : Int) : String {
            return when {
                time == DEFAULT_RESOURCE_AVG_TIME -> "Unknown"
                time < 60 -> "${time} min"
                else -> "${time / 60} hr"
            }
        }

        private fun formatRecommendationNo(recNo : Int) : String {
            return if (recNo == 1) "$recNo review" else "$recNo reviews"
        }

        private fun loadRatingImages(rating : Int, holder: ResourceViewHolder){
            when (rating) {
                in 1..5 -> { for (pos in 1..rating) { holder.ratingImageArray[pos - 1].setImageResource(R.drawable.thumbs_up) } }
                0 -> { for (pos in 0..4) { holder.ratingImageArray[pos].setImageResource(R.drawable.thumbs_down_blank) } }
                else -> {
                    for (pos in rating..-1) { holder.ratingImageArray[pos * (-1) - 1].setImageResource(R.drawable.thumbs_down) }
                    for (pos in -5..(rating - 1)) { holder.ratingImageArray[pos * (-1) - 1].setImageResource(R.drawable.thumbs_down_blank) }
                }
            }
        }

        private fun loadUrlImage(imageUrl : String?, holder : ResourceViewHolder ){
            if (imageUrl != null) {
                Glide.with(context).resumeRequests()
                Glide.with(context).load(imageUrl).priority(Priority.IMMEDIATE).crossFade().into(holder.imageIw)
            }
            else {
                Glide.with(context).resumeRequests()
                holder.imageIw.setImageResource(android.R.color.darker_gray)
            }
        }

        override fun getItemCount(): Int {
            return mResources.size
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ResourceViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.resource_card, viewGroup, false)
            return ResourceViewHolder(v)
        }

        override fun onBindViewHolder(holder: ResourceViewHolder, i: Int) {
            val resource = mResources[i]

            holder.titleTw.text = resource.title
            holder.topicTw.text = resource.topic
            holder.timeTw.text = formatAvgTime(resource.avgReqTime)
            holder.ratingCountTw.text = formatRecommendationNo(resource.recommendation_no)
            loadRatingImages(resource.rating.toInt(), holder)
            loadUrlImage(resource.imageUrl, holder)

            //Set Listeners
            holder.imageIw.setOnClickListener({ view ->
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
            })

            val userId = model.userId
            if(userId!= null) {
                holder.addToLibraryBtn.setOnClickListener({ view ->
                    model.addResourceToLibrary(userId, resource.id).observe(activity, LibraryResponseObserver())
                })
            }

        }

        inner class ResourceViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var imageIw: ImageView = itemView.findViewById(R.id.explore_resource_image)
            internal var titleTw: TextView = itemView.findViewById(R.id.explore_resource_title)
            internal var ratingLevel1: ImageView = itemView.findViewById(R.id.explore_resource_rating_level_1)
            internal var ratingLevel2: ImageView = itemView.findViewById(R.id.explore_resource_rating_level_2)
            internal var ratingLevel3: ImageView = itemView.findViewById(R.id.explore_resource_rating_level_3)
            internal var ratingLevel4: ImageView = itemView.findViewById(R.id.explore_resource_rating_level_4)
            internal var ratingLevel5: ImageView = itemView.findViewById(R.id.explore_resource_rating_level_5)
            internal var ratingCountTw: TextView = itemView.findViewById(R.id.explore_resource_rating_count)
            internal var topicTw: TextView = itemView.findViewById(R.id.explore_resource_topic)
            internal var timeTw: TextView = itemView.findViewById(R.id.explore_resource_time)
            internal var addToLibraryBtn: Button = itemView.findViewById(R.id.explore_resource_add_to_library)
            internal var ratingImageArray : Array<ImageView>

            init {
                ratingImageArray = arrayOf(ratingLevel1, ratingLevel2, ratingLevel3, ratingLevel4, ratingLevel5)

                if(model.userId == null) {
                    addToLibraryBtn.visibility = View.GONE
                }
            }
        }

        inner class LibraryResponseObserver : Observer<DalResponse> {
            override fun onChanged(response: DalResponse?) {
                if (response?.status == DalResponseStatus.SUCCESS) {
                    Toast.makeText(activity.application, activity.getString(
                            R.string.explore_resource_fragment_add_to_library_success_toast),
                            Toast.LENGTH_LONG).show()
                } else if (response?.status == DalResponseStatus.FAIL) {
                    val mSnackbar = Snackbar.make(edit_goal_layout, activity.getString(
                            R.string.explore_resource_fragment_add_to_library_fail_toast), Snackbar.LENGTH_SHORT)
                    mSnackbar.view.background = AppCompatResources.getDrawable(activity, R.color.snackbarErrorColor)
                    mSnackbar.show()
                }
            }
        }
    }
}