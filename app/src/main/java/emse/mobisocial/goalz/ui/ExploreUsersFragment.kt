package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.view.*
import android.widget.SearchView
import android.widget.TextView

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.UserMinimal
import emse.mobisocial.goalz.ui.viewModels.ExploreUsersViewModel

class ExploreUsersFragment : Fragment() {

    private lateinit var model : ExploreUsersViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var filterByRatingClicked: Boolean = false
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_explore_users, container, false)
        setHasOptionsMenu(true)

        // Initialize data
        model = ViewModelProviders.of(this).get(ExploreUsersViewModel::class.java)
        recyclerView = view.findViewById(R.id.explore_users_recycler_view) as RecyclerView

        setupRecyclerView()
        initializeObservers()

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = RecyclerViewAdapter(ArrayList<UserMinimal>())
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun initializeObservers() {
        model.usersList.observe(this, Observer<List<UserMinimal>> { users ->
            if (users != null) {
                recyclerViewAdapter.addItems(users)
            }
        })
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
                model.searchUsers(searchQuery!!)
                return true
            }

            override fun onQueryTextChange(searchQuery: String?): Boolean {
                model.searchUsers(searchQuery!!)
                return true
            }
        })

        // Set up the behaviour of the filter menu item
        val filterItem = menu!!.findItem(R.id.exploreFilter)
        val r = context.resources
        filterItem.setOnMenuItemClickListener(object: MenuItem.OnMenuItemClickListener {
            override fun onMenuItemClick(p0: MenuItem?): Boolean {
                if (!filterByRatingClicked) {
                    recyclerViewAdapter.filter()
                } else {
                    model.reset()
                }
                filterByRatingClicked = !filterByRatingClicked
                return true
            }
        })

        super.onCreateOptionsMenu(menu,inflater)
    }

    inner class RecyclerViewAdapter(usersParam: ArrayList<UserMinimal>) : RecyclerView.Adapter<RecyclerViewAdapter.UserViewHolder>() {
        private var mUsers: List<UserMinimal> = usersParam

        override fun getItemCount(): Int {
            return mUsers.size
        }

        fun addItems(newUsersList: List<UserMinimal>) {
            this.mUsers = newUsersList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UserViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.user_card, viewGroup, false)
            return UserViewHolder(v)
        }

        override fun onBindViewHolder(userViewHolder: UserViewHolder, i: Int) {
            // The data from the user model is retrieved and bound to the card View here.
            userViewHolder.userNickname.text = mUsers[i].nickname
            val rating = mUsers[i].rating
            when(rating){
                in 0..10 -> userViewHolder.ratingImage.setImageResource(R.drawable.level_1_cropped)
                in 11..50 -> userViewHolder.ratingImage.setImageResource(R.drawable.level_2_cropped)
                in 51..200 -> userViewHolder.ratingImage.setImageResource(R.drawable.level_3_cropped)
                else -> userViewHolder.ratingImage.setImageResource(R.drawable.level_4_cropped)
            }
            userViewHolder.rating.text = rating.toString()

            userViewHolder.itemView.setOnClickListener {
                val intent = Intent(activity, UserActivity::class.java)
                intent.putExtra("user_id", mUsers[i].id)
                intent.putExtra("user_nickname", mUsers[i].nickname)
                startActivity(intent)
            }
        }

        fun filter() {
             val filterdList = ArrayList<UserMinimal>(mUsers)
             filterdList.sortBy { user -> -user.rating }
             this.mUsers = filterdList
             notifyDataSetChanged()
        }

        inner class UserViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var userNickname: TextView = itemView.findViewById<View>(R.id.user_nickname) as TextView
            internal var ratingImage:ImageView = itemView.findViewById(R.id.rating_icon) as ImageView
            internal var rating:TextView = itemView.findViewById<View>(R.id.rating_text) as TextView
        }

    }
}
