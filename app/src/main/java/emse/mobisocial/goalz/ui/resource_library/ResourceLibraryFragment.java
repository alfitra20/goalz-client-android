package emse.mobisocial.goalz.ui.resource_library;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import emse.mobisocial.goalz.R;
import emse.mobisocial.goalz.model.Resource;
import emse.mobisocial.goalz.ui.viewModels.ResourceLibraryViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResourceLibraryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResourceLibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResourceLibraryFragment extends Fragment {

    ResourceLibraryViewModel model;
    RecyclerView mRecyclerView;
    ResourceLibraryAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ResourceLibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResourceLibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResourceLibraryFragment newInstance(String param1, String param2) {
        ResourceLibraryFragment fragment = new ResourceLibraryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Let's pre-connect to Chrome
        try {

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resource_library, container, false);
        model = ViewModelProviders.of(this).get(ResourceLibraryViewModel.class);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.resource_library_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) getActivity().finish();
        String userId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        model.getResourceLibrary(userId);
        initializeObservers(userId);
        // temporary, above and below
        //List<Resource> meh = new ArrayList<>();
        //tempData(meh);
        //mAdapter = new ResourceLibraryAdapter(getContext(), meh, new HashMap<String, String>());
        //mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    /*private void tempData(List<Resource> meh) {
        Resource resource = new Resource("1", "1", "https://www.bbcgoodfood.com/howto/guide/25-skills-every-cook-should-know",
                "Title", "Meh topic", -2.9, 5, 40);
        meh.add(resource);
        Resource resource2 = new Resource("2", "1", "https://www.wikihow.com/Code",
        "Title", "Meh topic",4.1, 5, 60);
        meh.add(resource2);
        Resource resource3 = new Resource("3", "1", "https://www.self.com/gallery/essential-weight-lifting-moves-for-beginners",
                "Title", "Meh topic", -2.4, 5, 25);
        meh.add(resource3);
        Resource resource4 = new Resource("4", "1", "http://dish.allrecipes.com/topping-and-baking-pizza/",
                "Title", "Meh topic", 3.5, 5, 30);
        meh.add(resource4);
        Resource resource5 = new Resource("5", "1", "http://www.bbc.com/news/in-pictures-43876420",
                "Title", "Meh topic", 0.5, 5, 30);
        meh.add(resource5);
        Resource resource6 = new Resource("6", "1", "https://bitcoin.org/en/",
                "Title", "Meh topic", 1.1, 5, 1);
        meh.add(resource6);
        Resource resource7 = new Resource("7", "1", "https://www.wikihow.com/Play-Guitar",
                "Title", "Meh topic", 2.3, 5, 400);
        meh.add(resource7);
        Resource resource8 = new Resource("8", "1", "https://www.yogajournal.com/meditation/7-meditations-relationship-problems",
                "Title", "Meh topic", -4.8, 5, 600);
        meh.add(resource8);
        Resource resource9 = new Resource("9", "1", "https://www.iwillteachyoutoberich.com/blog/how-to-make-money-fast/",
                "Title", "Meh topic", 3.6, 5, 60);
        meh.add(resource9);
        Resource resource10 = new Resource("10", "1", "http://www.abc.net.au/news/health/2017-09-16/yoga-a-beginner-guide/8656236",
                "Title", "Meh topic", -2.7, 5, 300);
        meh.add(resource10);
        Resource resource11 = new Resource("11", "1", "https://www.vogue.com/article/how-i-learned-to-ride-a-bike-as-an-adult",
                "Title", "Meh topic", 5, 5, 240);
        meh.add(resource11);
        Resource resource12 = new Resource("12", "1", "https://www.wikihow.com/Play-Basketball",
                "Title", "Meh topic", 0, 5, 500);
        meh.add(resource12);
    }*/

    private void initializeObservers(String userId) {
        model.resources.observe(this, resources -> {
            mAdapter = new ResourceLibraryAdapter(getContext(), resources, model, userId);
            mRecyclerView.setAdapter(mAdapter);
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.i("Resources library", "Closing the fragment!");
        /*Gson gson = new Gson();
        String hashMapString = gson.toJson(mAdapter.getImageUrls());
        SharedPreferences prefs = getContext().getSharedPreferences("imgUrls", Context.MODE_PRIVATE);
        prefs.edit().putString("imgUrls", hashMapString).apply();*/
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
