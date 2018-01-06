package com.vianet.lyricstadka.FragmentLyrics;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vianet.lyricstadka.Frag_Adaptor.LyricsAdaptor;
import com.vianet.lyricstadka.Frag_Adaptor.RecycleAdaptorCategory;
import com.vianet.lyricstadka.Frag_Adaptor.Suc_Cat_Frag_Adaptor;
import com.vianet.lyricstadka.Getter_Setter;
import com.vianet.lyricstadka.R;
import com.vianet.lyricstadka.network.AppControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LyricsList extends Fragment {


    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private TextView errorvolley;
    private int currentPage = 1;
    private LinearLayoutManager layoutmanager;
    private ArrayList<Getter_Setter> lyrics_list;
    private String count;
    private int item_per_page = 9;
    private String sub_cat_id;
    private String cat_id;
    private int total_num_page;
    private ProgressBar progressbar;
    private String url;
    public String urlcard;
    private LyricsAdaptor lyrics_adap;
    String sub_cat_name;
    private ImageView refreshImage;
    private String titlefortitle;

    public LyricsList() {
        // Required empty public constructor
    }


    /*   // TODO: Rename and change types and number of parameters
       public static LyricsList newInstance(String param1, String param2) {
           LyricsList fragment = new LyricsList();
           return fragment;
       }
   */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lyrics_list, container, false);

        layoutmanager = new LinearLayoutManager(getContext());

        //here we find cat_id , sub_cat_id , and sub_cat_name from Sub Activity
        final Bundle bundle = getArguments();
        sub_cat_name = bundle.getString("sub_cat_name");
        sub_cat_id = bundle.getString("idsubCat");
        cat_id = bundle.getString("idCat");


        recyclerView = (RecyclerView) view.findViewById(R.id.lyricsCatRecycle);
        errorvolley = (TextView) view.findViewById(R.id.error_text);
        progressbar = (ProgressBar) view.findViewById(R.id.progresBarL);
        refreshImage = (ImageView) view.findViewById(R.id.lyrics_list_refresh);

        //make call for lyrics
        if (lyrics_list == null) {
            makeLyricsRequest(1);
        } else {
            progressbar.setVisibility(View.GONE);
            lyrics_adap = new LyricsAdaptor(getContext(), lyrics_list);
            recyclerView.setLayoutManager(layoutmanager);
            recyclerView.setAdapter(lyrics_adap);
        }


        recyclerView.addOnItemTouchListener(new RecycleviewTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LyricsCard obj = new LyricsCard();

                // this url is used for fetching lyrics in LyricsCard Fragment
                urlcard = "http://63.142.254.250/lyrics_panel/API/webservice.php?action=LyricsRead&id=";

                //here we send data to Lyricscard Fragment
                Bundle bundle1 = new Bundle();
                bundle1.putString("sub_cat_name1", sub_cat_name);
                bundle1.putString("lyrics", lyrics_list.get(position).getText());
                bundle1.putString("id", urlcard + lyrics_list.get(position).getId());
                obj.setArguments(bundle1);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                ft.replace(R.id.conta, obj);
                ft.addToBackStack(null);
                ft.commit();

            }

         /*   @Override
            public void onLongClick(View view, int position) {

            }*/
        }));

        //here we handle the pagination of recycleview only 9 item show in one page
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                int c = Integer.parseInt(count);
                if (c % item_per_page == 0) {
                    total_num_page = c / item_per_page;
                } else {
                    total_num_page = c / item_per_page + 1;
                }
                int visibleItemCount = layoutmanager.getChildCount();
                int totalItemCount = layoutmanager.getItemCount();
                int firstVisibleItemPosition = layoutmanager.findFirstVisibleItemPosition();


                if (currentPage < total_num_page) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        currentPage++;
                        makeLyricsRequestPagination(currentPage);
//                        makeLyricsRequest(currentPage);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        refreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeLyricsRequest(1);
                refreshImage.setVisibility(View.GONE);
                errorvolley.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void makeLyricsRequest(final int currentPage) {

        url = "http://63.142.254.250/lyrics_panel/API/webservice.php?action=LyricsList&page=" + currentPage + "&cat=" + cat_id + "&subcat=" + sub_cat_id;

        progressbar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        StringRequest stringreq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response != null && response.length() >= 0) {

                    try {
                        lyrics_list = new ArrayList<>();

                        errorvolley.setVisibility(View.GONE);

                        JSONObject object = new JSONObject(response);
                        count = object.getString("count");
                        if (!count.isEmpty()) {
                            JSONArray jsonArray = object.getJSONArray("LyricsList");


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject lyricsData = jsonArray.getJSONObject(i);
                                Getter_Setter getSet = new Getter_Setter();
                                getSet.setText(lyricsData.getString("title"));
                                getSet.setId(lyricsData.getString("id"));
                                lyrics_list.add(getSet);
                            }

                            if (currentPage == 1) {

                                int resId = R.anim.layout_animation_fall_down;
                                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
                                lyrics_adap = new LyricsAdaptor(getContext(), lyrics_list);
                                recyclerView.setLayoutAnimation(animation);

                                recyclerView.setLayoutManager(layoutmanager);
                                recyclerView.setAdapter(lyrics_adap);
                            } else
                                lyrics_adap.notifyDataSetChanged();
                        } else {
                            errorvolley.setVisibility(View.VISIBLE);
//                        refreshImage.setVisibility(View.VISIBLE);
                            errorvolley.setText(R.string.data_notfound);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        errorvolley.setVisibility(View.VISIBLE);
//                        refreshImage.setVisibility(View.VISIBLE);
                        errorvolley.setText(R.string.data_notfound);

                    }

                } else {
                    errorvolley.setVisibility(View.VISIBLE);
                    refreshImage.setVisibility(View.VISIBLE);
                    errorvolley.setText(R.string.data_notfound);
                }


                progressbar.setVisibility(View.GONE);

                for (int i = 0; i < lyrics_list.size(); i++) {
                    Log.d("Respone Lyrics ", lyrics_list.get(i).getText());
                    Log.d("Count ", LyricsList.this.count);
                    Log.d(" page count ","lyrics list");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressbar.setVisibility(View.GONE);
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
        AppControllerSingleton.getMinstance().addToRequestQueue(stringreq);

    }

    private void makeLyricsRequestPagination(final int currentPage) {

        url = "http://63.142.254.250/lyrics_panel/API/webservice.php?action=LyricsList&page=" + currentPage + "&cat=" + cat_id + "&subcat=" + sub_cat_id;

        progressbar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        StringRequest stringreq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response != null && response.length() >= 0) {

                    try {

                        errorvolley.setVisibility(View.GONE);

                        JSONObject object = new JSONObject(response);
                        count = object.getString("count");
                        if (!count.isEmpty()) {
                            JSONArray jsonArray = object.getJSONArray("LyricsList");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject lyricsData = jsonArray.getJSONObject(i);
                                Getter_Setter getSet = new Getter_Setter();
                                getSet.setText(lyricsData.getString("title"));
                                getSet.setId(lyricsData.getString("id"));
                                lyrics_list.add(getSet);
                            }

                        } else {
                            Toast.makeText(getContext(), "Error in loading data", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error in loading data", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Error in loading data", Toast.LENGTH_SHORT).show();
                }
                progressbar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressbar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error in loading data", Toast.LENGTH_SHORT).show();
            }
        });
        AppControllerSingleton.getMinstance().addToRequestQueue(stringreq);
    }

    //class for handle recycler click event
    class RecycleviewTouchListener implements RecyclerView.OnItemTouchListener {
        private ClickListener clickListener;
        private GestureDetector gestureDet;

        RecycleviewTouchListener(Context context, final RecyclerView recyclerview, final ClickListener clicklistener) {
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


    public interface ClickListener {
        void onClick(View view, int position);

//        public void onLongClick(View view ,int position);
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
        titlefortitle = bundle.getString("sub_cat_name");


      /*  if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
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
