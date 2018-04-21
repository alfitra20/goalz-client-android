package emse.mobisocial.goalz.ui.resource_library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import emse.mobisocial.goalz.R;
import emse.mobisocial.goalz.model.Resource;

/**
 * Created by Sabina on 4/18/2018.
 */

public class ResourceLibraryAdapter extends RecyclerView.Adapter<ResourceLibraryAdapter.ResourceViewHolder> {

    private List<Resource> resources;

    public static class ResourceViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView link;
        public TextView topic;
        public ImageView image;
        public TextView avgReqTime;
        public TextView rating;

        public ResourceViewHolder(View v) {
            super(v);
            title = (TextView) itemView.findViewById(R.id.resource_title);
            topic = (TextView) itemView.findViewById(R.id.resource_topic);
            //link = (TextView) itemView.findViewById(R.id.resource_link);
            //avgReqTime = (TextView) itemView.findViewById(R.id.link);
            rating = (TextView) itemView.findViewById(R.id.resource_rating);
            // getting the image will be here
        }
    }

    public ResourceLibraryAdapter(List<Resource> resources) {
        this.resources = resources;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ResourceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // new view
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resource_card, viewGroup, false);
        return new ResourceViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ResourceViewHolder holder, int position) {
        holder.topic.setText(resources.get(position).getTopic());
        holder.title.setText(resources.get(position).getTitle());
        //holder.link.setText(resources.get(position).getLink());
        //holder.avgReqTime.setText(resources.get(position).getAvgReqTime());
        holder.rating.setText(String.valueOf(resources.get(position).getRating()));
        //holder.image.setImage
        // image will be here
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
}
