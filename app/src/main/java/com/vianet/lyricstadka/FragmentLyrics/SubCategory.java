package com.vianet.lyricstadka.FragmentLyrics;

import android.app.ActionBar;import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vianet.lyricstadka.Frag_Adaptor.Suc_Cat_Frag_Adaptor;
import com.vianet.lyricstadka.Getter_Setter;
import com.vianet.lyricstadka.R;
import com.vianet.lyricstadka.network.AppControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubCategory.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubCategory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubCategory extends Fragment {


    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    String cat_id;
    private ArrayList<Getter_Setter> sub_cat_list;
    private String url;
    private Suc_Cat_Frag_Adaptor sub_cat_adap;
    private TextView errorvolley;
    ProgressBar progressBar;
    private ImageView refreshImage;
    String titlefortitle;


    public SubCategory() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SubCategory newInstance(String id) {
        SubCategory fragment = new SubCategory();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(titlefortitle);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);

        //here we get cat id from main activity for hitting url category wise
        Bundle bundle = getArguments();
        cat_id = bundle.getString("idcat");
        Log.d("CAT_ID_IN_SUB_CAT", cat_id);
        url = "http://63.142.254.250/lyrics_panel/API/webservice.php?action=SubCategoryList&catid=" + cat_id;

        recyclerView = (RecyclerView) view.findViewById(R.id.subcategoryRecy);
        errorvolley = (TextView) view.findViewById(R.id.Error_text);
        progressBar = (ProgressBar) view.findViewById(R.id.progresBarS);
        refreshImage= (ImageView) view.findViewById(R.id.sub_cat_refresh);


        if (sub_cat_list == null) {
            makeSubCatList();
        } else {
            progressBar.setVisibility(View.GONE);
            sub_cat_adap = new Suc_Cat_Frag_Adaptor(getContext(), sub_cat_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(sub_cat_adap);
        }


        // Select RecyclerView Item
        recyclerView.addOnItemTouchListener(new RecycleviewTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                LyricsList obj = new LyricsList();
                Bundle bundle = new Bundle();
                bundle.putString("sub_cat_name", sub_cat_list.get(position).getText());
                bundle.putString("idsubCat", sub_cat_list.get(position).getSubid());
                bundle.putString("idCat", cat_id);
                obj.setArguments(bundle);

                FragmentTransaction fm = getFragmentManager().beginTransaction();
                fm.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.pop_exit);
                fm.replace(R.id.conta,obj);
                fm.addToBackStack(null);
                fm.commit();

            }

        /*    @Override
            public void onLongClick(View view, int position) {

            }*/
        }));

        refreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSubCatList();
                refreshImage.setVisibility(View.GONE);
                errorvolley.setVisibility(View.GONE);
            }
        });

        return view;
    }

    // Make Call For Subcategory Request
    private void makeSubCatList() {
//        progressDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response != null && response.length() >= 0) {

                    try {
                        sub_cat_list = new ArrayList<>();
                        errorvolley.setVisibility(View.GONE);
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray("subcategory");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Getter_Setter getSet = new Getter_Setter();
                            JSONObject subCatData = jsonArray.getJSONObject(i);

                            getSet.setText(subCatData.getString("sub_cat_name"));
                            getSet.setSubid(subCatData.getString("id"));
                            sub_cat_list.add(getSet);
                        }

                        int resId=R.anim.layout_animation_fall_down;
                        LayoutAnimationController animation= AnimationUtils.loadLayoutAnimation(getContext(),resId);
                        sub_cat_adap = new Suc_Cat_Frag_Adaptor(getContext(), sub_cat_list);
                        recyclerView.setLayoutAnimation(animation);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(sub_cat_adap);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        recyclerView.setVisibility(View.GONE);
                        errorvolley.setVisibility(View.VISIBLE);
//                        refreshImage.setVisibility(View.VISIBLE);
                        errorvolley.setText(R.string.data_notfound);
                    }


                } else {
                    recyclerView.setVisibility(View.GONE);
                    errorvolley.setVisibility(View.VISIBLE);
                    refreshImage.setVisibility(View.VISIBLE);
                    errorvolley.setText("Data Not Available");
                }



                progressBar.setVisibility(View.GONE);
             /*   for (int i=0;i<sub_cat_list.size();i++){
                    Log.d("Respose ",sub_cat_list.get(i).getText());
                    Log.d("SUB_CAT ID",sub_cat_list.get(i).getSubid());
                }*/

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                errorvolley.setVisibility(View.VISIBLE);
                refreshImage.setVisibility(View.VISIBLE);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    errorvolley.setText(R.string.Error_TimeOut);

                } else if (error instanceof AuthFailureError) {
                    errorvolley.setText(R.string.AuthFailure);

                } else if (error instanceof ServerError) {
                    errorvolley.setText(R.string.Server_Error);

                } else if (error instanceof NetworkError) {
                    errorvolley.setText(R.string.Network);

                } else if (error instanceof ParseError) {
                    errorvolley.setText(R.string.Parsing_error);
                }
            }
        });
        AppControllerSingleton.getMinstance().addToRequestQueue(stringRequest);
    }


    // Handling Recycle View
    class RecycleviewTouchListener implements RecyclerView.OnItemTouchListener {
        private ClickListener clickListener;
        private GestureDetector gestureDet;

        public RecycleviewTouchListener(Context context, final RecyclerView recyclerview, final ClickListener clicklistener) {
            this.clickListener = clicklistener;
            gestureDet = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

             /*   @Override
                public void onLongPress(MotionEvent e) {
                    View view=recyclerview.findChildViewUnder(e.getX(),e.getY());
                    if (view!=null && clicklistener!=null){
                        clicklistener.onLongClick(view,recyclerview.getChildPosition(view));
                    }
                }*/
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View view = rv.findChildViewUnder(e.getX(), e.getY());
            if (view != null && clickListener != null && gestureDet.onTouchEvent(e)) {
                clickListener.onClick(view, rv.getChildPosition(view));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    public static interface ClickListener {
        public void onClick(View view, int position);

    /*    public void onLongClick(View view ,int position);*/
    }











/*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle bundle = getArguments();
        titlefortitle = bundle.getString("title");

        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
