package com.vianet.lyricstadka.FragmentLyrics;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.vianet.lyricstadka.Getter_Setter;
import com.vianet.lyricstadka.R;
import com.vianet.lyricstadka.network.AppControllerSingleton;
import com.vianet.lyricstadka.network.ItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LyricsList extends Fragment implements ItemClickListener {

    public String urlcard;
    String sub_cat_name;
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
    private LyricsAdaptor lyrics_adap;
    private ImageView refreshImage;
    private String titlefortitle;
    private ProgressBar bottomProgreshBar;

    public LyricsList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
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
        bottomProgreshBar = (ProgressBar) view.findViewById(R.id.bottamBarProgressBar);

        //make call for lyrics
        if (lyrics_list == null) {
            makeLyricsRequest(1);

        } else {
            progressbar.setVisibility(View.GONE);
            lyrics_adap = new LyricsAdaptor(getContext(), lyrics_list);
            recyclerView.setLayoutManager(layoutmanager);
            recyclerView.setAdapter(lyrics_adap);
            lyrics_adap.setClickListener(this);
        }

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

                int lastvisibleitem = layoutmanager.findLastCompletelyVisibleItemPosition();
                if (!progressbar.isShown()) {
                    if (currentPage < total_num_page) {
                        if (lastvisibleitem == lyrics_list.size() - 1) {
                            currentPage++;
                            makeLyricsRequestPagination(currentPage);

                        }
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

//        Log.d("cat id ", cat_id);
//        Log.d("sub cat id: ", sub_cat_id);
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
                        if (!count.equals("0")) {


                            JSONArray jsonArray = object.getJSONArray("LyricsList");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject lyricsData = jsonArray.getJSONObject(i);
                                Getter_Setter getSet = new Getter_Setter();
                                getSet.setText(lyricsData.getString("title"));
                                getSet.setId(lyricsData.getString("id"));
                                lyrics_list.add(getSet);
                            }

                            if (currentPage == 1) {
                                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(AppControllerSingleton.getMinstance(), R.anim.layout_animation_fall_down);
                                lyrics_adap = new LyricsAdaptor(getContext(), lyrics_list);
                                if (animation != null) {
                                    recyclerView.setLayoutAnimation(animation);
                                }
                                recyclerView.setLayoutManager(layoutmanager);
                                recyclerView.setAdapter(lyrics_adap);
                                lyrics_adap.setClickListener(LyricsList.this);

                            } else
                                lyrics_adap.notifyDataSetChanged();
                        } else {

                            errorvolley.setVisibility(View.VISIBLE);
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

    private void makeLyricsRequestPagination(int currentPage) {

//        Log.d("page", String.valueOf(currentPage));
        url = "http://63.142.254.250/lyrics_panel/API/webservice.php?action=LyricsList&page=" + currentPage + "&cat=" + cat_id + "&subcat=" + sub_cat_id;

//        progressbar.setVisibility(View.VISIBLE);
        bottomProgreshBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

//        Log.d("makeLyricsRequestPagina","makeLyricsRequestPagination");

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
                            lyrics_adap.notifyDataSetChanged();
                            lyrics_adap.setClickListener(LyricsList.this);
                            bottomProgreshBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getContext(), "Error in loading data", Toast.LENGTH_SHORT).show();
                            bottomProgreshBar.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error in loading data", Toast.LENGTH_SHORT).show();
                        bottomProgreshBar.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(getContext(), "Error in loading data", Toast.LENGTH_SHORT).show();
                    bottomProgreshBar.setVisibility(View.GONE);
                }
                progressbar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                progressbar.setVisibility(View.GONE);
                bottomProgreshBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error in loading data", Toast.LENGTH_SHORT).show();
            }
        });
        AppControllerSingleton.getMinstance().addToRequestQueue(stringreq);
    }

    @Override
    public void onClick(int position) {
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle bundle = getArguments();
        titlefortitle = bundle.getString("sub_cat_name");
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(titlefortitle);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDetach() {

        AppControllerSingleton.getMinstance().getmRequestQueue().cancelAll(true);
        super.onDetach();
    }

}
