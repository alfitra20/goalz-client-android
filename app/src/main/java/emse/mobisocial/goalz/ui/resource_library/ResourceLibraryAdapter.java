package emse.mobisocial.goalz.ui.resource_library;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;

import emse.mobisocial.goalz.R;
import emse.mobisocial.goalz.model.Resource;
import emse.mobisocial.goalz.model.ResourceKt;
import emse.mobisocial.goalz.ui.viewModels.ResourceLibraryViewModel;
import emse.mobisocial.goalz.util.ImageExtractor;

public class ResourceLibraryAdapter extends RecyclerView.Adapter<ResourceLibraryAdapter.ResourceViewHolder> {
    ResourceLibraryViewModel model;
    private Context context;
    private List<Resource> resources;
    public static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    String userId;

    public static class ResourceViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        public TextView title;
        //public TextView link;
        public TextView topic;
        public ImageView image;
        public TextView avgReqTime;
        //public TextView avgReqTimeText;
        public ImageView rating_level1;
        public ImageView rating_level2;
        public ImageView rating_level3;
        public ImageView rating_level4;
        public ImageView rating_level5;
        public TextView resource_rating_count;
        public Button deleteButton;
        public Button addToGoalButton;
        //public View frameLayout;
        //
        //CustomTabsServiceConnection mCustomTabsServiceConnection;
        //CustomTabsSession mCustomTabsSession;
        //CustomTabsClient mClient;
        public ImageView timeIcon;

        public ResourceViewHolder(View v) {
            super(v);
            title = (TextView) itemView.findViewById(R.id.resource_title);
            topic = (TextView) itemView.findViewById(R.id.resource_topic);
            //link = (TextView) itemView.findViewById(R.id.resource_link);
            image = (ImageView) itemView.findViewById(R.id.resource_image);
            avgReqTime = (TextView) itemView.findViewById(R.id.resource_time);
            rating_level1 = (ImageView) itemView.findViewById(R.id.resource_rating_level_1);
            rating_level2 = (ImageView) itemView.findViewById(R.id.resource_rating_level_2);
            rating_level3 = (ImageView) itemView.findViewById(R.id.resource_rating_level_3);
            rating_level4 = (ImageView) itemView.findViewById(R.id.resource_rating_level_4);
            rating_level5 = (ImageView) itemView.findViewById(R.id.resource_rating_level_5);
            timeIcon = (ImageView) itemView.findViewById(R.id.resource_time_icon);
            resource_rating_count = (TextView) itemView.findViewById(R.id.resource_rating_count);
            deleteButton = (Button) itemView.findViewById(R.id.delete_button);
           // frameLayout = (FrameLayout) itemView.findViewById(R.id.resource_frame);
        }

        /*@Override
        public void onClick(View view) {
            Log.i("CLICK!", "WHEEEEE!");
        }*/
    }

    public ResourceLibraryAdapter(Context context, List<Resource> resources,
                                  ResourceLibraryViewModel model, String userId) {
        this.context = context;
        this.resources = resources;
        this.model = model;
        this.userId = userId;

        /*CustomTabsClient.bindCustomTabsService(context, CUSTOM_TAB_PACKAGE_NAME, new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                // mClient is now valid.
                mClient = client;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // mClient is no longer valid. This also invalidates sessions.
                mClient = null;
            }
        });*/
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
        //holder.image.setImageResource(null);
        holder.topic.setText(resources.get(position).getTopic());
        holder.title.setText(resources.get(position).getTitle());



        /*String link = resources.get(position).getLink();
        int i1 = link.indexOf("//")+2;
        int i2 = link.indexOf('/', i1);
        link = link.substring(i1, i2);
        if (!link.startsWith("www")) {
            link = "www." + link;
        }*/
        //holder.link.setText(link);

        int avgReqTime = resources.get(position).getAvgReqTime();
        if(avgReqTime == ResourceKt.DEFAULT_RESOURCE_AVG_TIME) {
            holder.avgReqTime.setText("");
            holder.timeIcon.setImageDrawable(null);
        } else if(avgReqTime/60 < 1) { // less than an hour
            /*if(avgReqTime == 1) {
                holder.avgReqTimeText.setText("minute");
            } else {
                holder.avgReqTimeText.setText("minutes");
            }*/
            //holder.avgReqTimeText.setText("min");
            holder.avgReqTime.setText(String.valueOf(avgReqTime) + " min");
            holder.timeIcon.setImageResource(R.drawable.clock_outline2);
        } else {
            avgReqTime /= 60;
            /*if(avgReqTime == 1) {
                holder.avgReqTimeText.setText("hour");
            } else {
                holder.avgReqTimeText.setText("hours");
            }*/
            //holder.avgReqTimeText.setText("hr");
            holder.avgReqTime.setText("  "+String.valueOf(avgReqTime) + " hr");
            holder.timeIcon.setImageResource(R.drawable.clock_outline2);
        }


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

        if(resources.get(position).getRecommendation_no() == 1) {
            holder.resource_rating_count.setText(resources.get(position).getRecommendation_no()+" review");
        } else {
            holder.resource_rating_count.setText(resources.get(position).getRecommendation_no()+" reviews");
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

       /* Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("UNICORNS!!", "running in the thread");
                holder.mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
                    @Override
                    public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                        holder.mClient = customTabsClient;
                        holder.mClient.warmup(0);
                        holder.mCustomTabsSession = holder.mClient.newSession(null);
                        Log.i("UNICORNS!", "so, now client is created");
                        holder.mCustomTabsSession.mayLaunchUrl(Uri.parse(resources.get(position).getLink()), null, null);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        Log.e("TAG","onServiceDisconnected");
                        holder.mClient = null;
                    }
                };
                Log.i("UNICORNS!!", "let's bind the service");
                CustomTabsClient.bindCustomTabsService(context, CUSTOM_TAB_PACKAGE_NAME, holder.mCustomTabsServiceConnection);
            }
        });
        thread.setName("Fckin thread");
        thread.start();*/

        holder.image.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Intent intent = new Intent(context, WebViewActivity.class);
                //intent.putExtra("url", resources.get(position).getLink());
                //context.startActivity(intent);
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.resource_click));
                try {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    //builder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
                    //builder.setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right);
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(context, Uri.parse(resources.get(position).getLink()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Intent intent = new Intent(context, WebViewActivity.class); // if there is no google chrome browser
                    intent.putExtra("url", resources.get(position).getLink());
                    context.startActivity(intent);
                }
            }
        });
        /*holder.link.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.resource_click));
                try {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(context, Uri.parse(resources.get(position).getLink()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Intent intent = new Intent(context, WebViewActivity.class); // if there is no google chrome browser
                    intent.putExtra("url", resources.get(position).getLink());
                    context.startActivity(intent);
                }
            }
        });*/

        holder.deleteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Resource Library");

                builder.setMessage("Are you sure you want to delete the resource?");
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        LiveData response = model.deleteResource(userId, resources.get(position).getId());
                        CharSequence text = "Resource deleted";

                        resources.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());

                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.rgb(82, 190, 128));
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.rgb(203, 67, 53));
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
