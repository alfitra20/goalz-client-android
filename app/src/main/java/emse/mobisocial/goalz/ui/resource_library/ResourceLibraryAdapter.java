package emse.mobisocial.goalz.ui.resource_library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import emse.mobisocial.goalz.R;
import emse.mobisocial.goalz.model.ResourceTemplate;

/**
 * Created by Sabina on 4/18/2018.
 */

public class ResourceLibraryAdapter extends RecyclerView.Adapter<ResourceLibraryAdapter.ResourceViewHolder>
        implements Filterable {

    private Context context;
    private List<ResourceTemplate> resources;
    private List<ResourceTemplate> resourcesFiltered;
    private ResourceLibraryAdapterListener resourceLibraryAdapterListener;

    public static class ResourceViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView link;
        public TextView topic;
        //public TextView tweetText;
        //public TextView tweetDate;

        public ResourceViewHolder(View v) {
            super(v);
            title = (TextView) itemView.findViewById(R.id.title);
            topic = (TextView) itemView.findViewById(R.id.topic);
            link = (TextView) itemView.findViewById(R.id.link);
        }
    }

    public ResourceLibraryAdapter(Context context, List<ResourceTemplate> resources,
                                  ResourceLibraryAdapterListener resourceLibraryAdapterListener) {
        this.context = context;
        this.resources = resources;
        this.resourcesFiltered = resources;
        this.resourceLibraryAdapterListener = resourceLibraryAdapterListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ResourceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // new view
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resource_row_item, viewGroup, false);
        return new ResourceViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ResourceViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.topic.setText(resources.get(position).getTopic());
        holder.title.setText(resources.get(position).getTitle());
        holder.link.setText(resources.get(position).getLink());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return resources.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    resourcesFiltered = resources;
                } else {
                    List<ResourceTemplate> filteredList = new ArrayList<>();
                    for (ResourceTemplate row : resources) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getTopic().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    resourcesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = resourcesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                resourcesFiltered = (ArrayList<ResourceTemplate>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ResourceLibraryAdapterListener {
        void onResourceSelected(ResourceTemplate contact);
    }
}
