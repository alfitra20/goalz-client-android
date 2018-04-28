package emse.mobisocial.goalz.ui.resource_library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import emse.mobisocial.goalz.R;
import emse.mobisocial.goalz.model.Resource;

public class ResourceLibraryAdapter extends RecyclerView.Adapter<ResourceLibraryAdapter.ResourceViewHolder>
        /* implements View.OnClickListener*/ {
    Context context;
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
            link = (TextView) itemView.findViewById(R.id.resource_link);
            rating = (TextView) itemView.findViewById(R.id.resource_rating);
            image = (ImageView) itemView.findViewById(R.id.resource_image);
            avgReqTime = (TextView) itemView.findViewById(R.id.resource_time);
        }
    }

    public ResourceLibraryAdapter(Context context, List<Resource> resources) {
        this.context = context;
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
        Glide.with(context).pauseRequests(); //doesn't make sense to me but it solves the problem, at least partly
        if (holder.image != null) {
            Glide.clear(holder.image); // because fckin magic!
            holder.image.setImageResource(R.drawable.light);
        }

        holder.topic.setText(resources.get(position).getTopic());
        holder.title.setText(resources.get(position).getTitle());
        holder.rating.setText(String.valueOf(resources.get(position).getRating()));
        holder.link.setText(resources.get(position).getLink());
        holder.avgReqTime.setText(String.valueOf(resources.get(position).getAvgReqTime()));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    String imgUrl = ImageExtractor.extractImageUrl(resources.get(position).getLink());
                    // now after the image is ready, let's add the card to UI

                    if (imgUrl != null && holder.image.getMeasuredWidth() *
                            holder.image.getMeasuredHeight() != 0) {  // because sh*t happens!! :(
                        Runnable updateViewRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(context).resumeRequests();
                                Glide.with(context)
                                        .load(imgUrl)
                                        .priority(Priority.IMMEDIATE)
                                        .crossFade()
                                        //.override(holder.image.getMeasuredWidth(), holder.image.getMeasuredHeight())
                                        //.centerCrop()
                                        //.diskCacheStrategy(DiskCacheStrategy.ALL) this shitty cache produced more problems than solved!
                                        .into(holder.image);
                            }
                        };
                        Handler handler = new Handler(context.getApplicationContext().getMainLooper());
                        handler.post(updateViewRunnable);
                    } else {
                        /*Runnable updateViewRunnable = new Runnable() {
                            @Override
                            public void run() {
                                holder.image.setImageResource(R.drawable.light);
                            }
                        };
                        Handler handler = new Handler(context.getApplicationContext().getMainLooper());
                        handler.post(updateViewRunnable);*/
                        //holder.image.setImageResource(android.R.color.transparent);
                        //Glide.clear(holder.image);
                        //holder.image.setImageURI(null);
                        //holder.image.setImageDrawable(null);
                    }
                } catch (Exception e) {
                    //Glide.clear(holder.image);
                    //holder.image.setImageDrawable(null);
                    /*Runnable updateViewRunnable = new Runnable() {
                        @Override
                        public void run() {
                            holder.image.setImageResource(R.drawable.light);
                        }
                    };
                    Handler handler = new Handler(context.getApplicationContext().getMainLooper());
                    handler.post(updateViewRunnable);*/
                    e.printStackTrace();
                }
            }
        });

        thread.setName("Fckin thread");
        thread.start();
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
