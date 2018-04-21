package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.ui.viewModels.ExploreResourcesViewModel
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup
import com.nex3z.togglebuttongroup.button.LabelToggle

class ExploreResourcesFragment : Fragment() {

    private lateinit var model : ExploreResourcesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    // filter variables and views
    private var filterOpen: Boolean = false
    private lateinit var filterView: MultiSelectToggleGroup
    private lateinit var topicFilter: LabelToggle
    private lateinit var ratingFilter: LabelToggle
    private lateinit var timeFilter: LabelToggle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_explore_resources, container, false)
        setHasOptionsMenu(true)

        // Initialize data
        filterView = view.findViewById(R.id.resources_filters) as MultiSelectToggleGroup
        model = ViewModelProviders.of(this).get(ExploreResourcesViewModel::class.java)
        recyclerView = view.findViewById(R.id.explore_resources_recycler_view) as RecyclerView
        topicFilter = view.findViewById(R.id.order_resources_topic)
        ratingFilter = view.findViewById(R.id.order_resources_rating)
        timeFilter = view.findViewById(R.id.order_resources_time)

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
                recyclerViewAdapter.addItems(resources)
            }
        })
    }

    private fun initializeListeners() {
        filterView.setOnCheckedChangeListener(object: MultiSelectToggleGroup.OnCheckedStateChangeListener {
            override fun onCheckedStateChanged(single: MultiSelectToggleGroup?, checkedId: Int, isChecked: Boolean) {
                if (isChecked) {
                    uncheckOthers(checkedId)
                    recyclerViewAdapter.filterRecyclerView()
                } else {
                    model.reset()
                }
            }
        })
    }

    private fun uncheckOthers(checkId: Int) {
        for (id: Int in filterView.getCheckedIds()) {
            if (id != checkId) {
                when(id) {
                    ratingFilter.id -> ratingFilter.setChecked(false)
                    topicFilter.id -> topicFilter.setChecked(false)
                    timeFilter.id -> timeFilter.setChecked(false)
                }
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
        val topMarginPx = r.getDimensionPixelSize(R.dimen.explore_top_margin)
        val params = recyclerView.getLayoutParams() as ViewGroup.MarginLayoutParams
        filterItem.setOnMenuItemClickListener(object: MenuItem.OnMenuItemClickListener {
            override fun onMenuItemClick(p0: MenuItem?): Boolean {
                if (!filterOpen) {
                    filterView.visibility = View.VISIBLE
                    params.setMargins(0, 0, 0, 0)
                    recyclerView.setLayoutParams(params)
                } else {
                    filterView.visibility = View.GONE
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

        fun filterRecyclerView() {
            val checkedIds = filterView.getCheckedIds()
            for (id: Int in checkedIds) {
                when (id) {
                    ratingFilter.id -> filterByRating()
                    topicFilter.id -> filterByTopic()
                    timeFilter.id -> filterByTime()
                }
            }
        }

        fun filterByTopic() {
            val filterdList = ArrayList<Resource>(mResources)
            filterdList.sortBy { resource -> resource.topic }
            this.mResources = filterdList
            notifyDataSetChanged()
        }

        fun filterByRating() {
            val filterdList = ArrayList<Resource>(mResources)
            filterdList.sortBy { resource -> -resource.rating }
            this.mResources = filterdList
            notifyDataSetChanged()
        }

        fun filterByTime() {
            val filterdList = ArrayList<Resource>(mResources)
            filterdList.sortBy { resource -> resource.avgReqTime }
            this.mResources = filterdList
            notifyDataSetChanged()
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