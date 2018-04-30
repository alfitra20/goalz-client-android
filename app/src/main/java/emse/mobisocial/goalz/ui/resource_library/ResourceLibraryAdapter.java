package emse.mobisocial.goalz.ui.resource_library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;

import java.util.HashMap;
import java.util.List;

import emse.mobisocial.goalz.R;
import emse.mobisocial.goalz.model.Resource;
import emse.mobisocial.goalz.util.ImageExtractor;

public class ResourceLibraryAdapter extends RecyclerView.Adapter<ResourceLibraryAdapter.ResourceViewHolder> {
    private Context context;
    private List<Resource> resources;
    private HashMap imageUrls;

    public static class ResourceViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        public TextView title;
        public TextView link;
        public TextView topic;
        public ImageView image;
        public TextView avgReqTime;
        public TextView avgReqTimeText;
        public ImageView rating_level1;
        public ImageView rating_level2;
        public ImageView rating_level3;
        public ImageView rating_level4;
        public ImageView rating_level5;
        public View frameLayout;

        public ResourceViewHolder(View v) {
            super(v);
            title = (TextView) itemView.findViewById(R.id.resource_title);
            topic = (TextView) itemView.findViewById(R.id.resource_topic);
            link = (TextView) itemView.findViewById(R.id.resource_link);
            image = (ImageView) itemView.findViewById(R.id.resource_image);
            avgReqTime = (TextView) itemView.findViewById(R.id.resource_time);
            avgReqTimeText = (TextView) itemView.findViewById(R.id.resource_time_word);
            rating_level1 = (ImageView) itemView.findViewById(R.id.resource_rating_level_1);
            rating_level2 = (ImageView) itemView.findViewById(R.id.resource_rating_level_2);
            rating_level3 = (ImageView) itemView.findViewById(R.id.resource_rating_level_3);
            rating_level4 = (ImageView) itemView.findViewById(R.id.resource_rating_level_4);
            rating_level5 = (ImageView) itemView.findViewById(R.id.resource_rating_level_5);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.resource_frame);
        }

        /*@Override
        public void onClick(View view) {
            Log.i("CLICK!", "WHEEEEE!");
        }*/
    }

    public ResourceLibraryAdapter(Context context, List<Resource> resources, HashMap<String, String> imageUrls) {
        this.context = context;
        this.resources = resources;
        this.imageUrls =  imageUrls;
    }

    public HashMap getImageUrls() {
        return imageUrls;
    }

    @Override
    public ResourceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resource_library_card, viewGroup, false);
        return new ResourceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ResourceViewHolder holder, int position) {
        Glide.with(context).pauseRequests(); //doesn't make sense to me but it solves the problem, at least partly
        Glide.clear(holder.image); // because fckin magic!
        holder.image.setImageResource(R.drawable.light);
        holder.topic.setText(resources.get(position).getTopic());
        holder.title.setText(resources.get(position).getTitle());

        String link = resources.get(position).getLink();
        int i1 = link.indexOf("//")+2;
        int i2 = link.indexOf('/', i1);
        link = link.substring(i1, i2);
        if (!link.startsWith("www")) {
            link = "www." + link;
        }
        holder.link.setText(link);

        int avgReqTime = resources.get(position).getAvgReqTime();
        if(avgReqTime/60 < 1) { // less than an hour
            if(avgReqTime == 1) {
                holder.avgReqTimeText.setText("minute");
            } else {
                holder.avgReqTimeText.setText("minutes");
            }
        } else {
            avgReqTime /= 60;
            if(avgReqTime == 1) {
                holder.avgReqTimeText.setText("hour");
            } else {
                holder.avgReqTimeText.setText("hours");
            }
        }
        holder.avgReqTime.setText(String.valueOf(avgReqTime));

        // setting rating:
        int rating = (int) Math.round(resources.get(position).getRating());
        if (resources.get(position).getRating() > 0) {
            switch (rating) {
                case 1:
                    holder.rating_level1.setImageResource(R.drawable.thumbs_up);
                    break;
                case 2: {
                    holder.rating_level1.setImageResource(R.drawable.thumbs_up);
                    holder.rating_level2.setImageResource(R.drawable.thumbs_up);
                    break;
                }
                case 3: {
                    holder.rating_level1.setImageResource(R.drawable.thumbs_up);
                    holder.rating_level2.setImageResource(R.drawable.thumbs_up);
                    holder.rating_level3.setImageResource(R.drawable.thumbs_up);
                    break;
                }
                case 4: {
                    holder.rating_level1.setImageResource(R.drawable.thumbs_up);
                    holder.rating_level2.setImageResource(R.drawable.thumbs_up);
                    holder.rating_level3.setImageResource(R.drawable.thumbs_up);
                    holder.rating_level4.setImageResource(R.drawable.thumbs_up);
                    break;
                }
                case 5: {
                    holder.rating_level1.setImageResource(R.drawable.thumbs_up);
                    holder.rating_level2.setImageResource(R.drawable.thumbs_up);
                    holder.rating_level3.setImageResource(R.drawable.thumbs_up);
                    holder.rating_level4.setImageResource(R.drawable.thumbs_up);
                    holder.rating_level5.setImageResource(R.drawable.thumbs_up);
                }
            }
        } else if (resources.get(position).getRating() < 0) {
            switch (rating) {
                case 0: {
                    holder.rating_level1.setImageResource(R.drawable.thumbs_down_blank);
                    holder.rating_level2.setImageResource(R.drawable.thumbs_down_blank);
                    holder.rating_level3.setImageResource(R.drawable.thumbs_down_blank);
                    holder.rating_level4.setImageResource(R.drawable.thumbs_down_blank);
                    holder.rating_level5.setImageResource(R.drawable.thumbs_down_blank);
                }
                case -1: {
                    holder.rating_level1.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level2.setImageResource(R.drawable.thumbs_down_blank);
                    holder.rating_level3.setImageResource(R.drawable.thumbs_down_blank);
                    holder.rating_level4.setImageResource(R.drawable.thumbs_down_blank);
                    holder.rating_level5.setImageResource(R.drawable.thumbs_down_blank);
                    break;
                }
                case -2: {
                    holder.rating_level1.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level2.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level3.setImageResource(R.drawable.thumbs_down_blank);
                    holder.rating_level4.setImageResource(R.drawable.thumbs_down_blank);
                    holder.rating_level5.setImageResource(R.drawable.thumbs_down_blank);
                    break;
                }
                case -3: {
                    holder.rating_level1.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level2.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level3.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level4.setImageResource(R.drawable.thumbs_down_blank);
                    holder.rating_level5.setImageResource(R.drawable.thumbs_down_blank);
                    break;
                }
                case -4: {
                    holder.rating_level1.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level2.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level3.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level4.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level5.setImageResource(R.drawable.thumbs_down_blank);
                    break;
                }
                case -5: {
                    holder.rating_level1.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level2.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level3.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level4.setImageResource(R.drawable.thumbs_down);
                    holder.rating_level5.setImageResource(R.drawable.thumbs_down);
                    break;
                }
            }
        }

        String imageUrl = resources.get(position).getImageUrl();
        if(imageUrl != null){
            Glide.with(context).resumeRequests();
            Glide.with(context)
                    .load(imageUrl)
                    .priority(Priority.IMMEDIATE)
                    .crossFade()
                    //.override(holder.image.getMeasuredWidth(), holder.image.getMeasuredHeight())
                    //.centerCrop()
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.image);
        }

        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    String imgUrl;
                    if(imageUrls.containsKey(resources.get(position).getId())) {
                        imgUrl = (String) imageUrls.get(resources.get(position).getId());
                    } else {
                        imgUrl = ImageExtractor.extractImageUrl(resources.get(position).getLink());
                        imageUrls.put(resources.get(position).getId(), imgUrl);
                    }

                    // now after the image url is ready, let's add it to UI
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
                                        //.diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(holder.image);
                            }
                        };
                        Handler handler = new Handler(context.getApplicationContext().getMainLooper());
                        handler.post(updateViewRunnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setName("Fckin thread");
        thread.start();*/

        holder.frameLayout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Intent intent = new Intent(context, WebViewActivity.class);
                //intent.putExtra("url", resources.get(position).getLink());
                //context.startActivity(intent);
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(resources.get(position).getLink()));
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }
}
