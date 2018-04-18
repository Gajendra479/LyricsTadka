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
import com.vianet.lyricstadka.network.ItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubCategory extends Fragment implements ItemClickListener {

    String cat_id;
    ProgressBar progressBar;
    String titlefortitle;
    StringRequest stringRequest;
    private RecyclerView recyclerView;
    private ArrayList<Getter_Setter> sub_cat_list;
    private String url;
    private Suc_Cat_Frag_Adaptor sub_cat_adap;
    private TextView errorvolley;
    private ImageView refreshImage;


    public SubCategory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);

        //here we get cat id from main activity for hitting url category wise
        Bundle bundle = getArguments();
        cat_id = bundle.getString("idcat");
//        Log.d("CAT_ID_IN_SUB_CAT", cat_id);
        url = "http://63.142.254.250/lyrics_panel/API/webservice.php?action=SubCategoryList&catid=" + cat_id;

        recyclerView = (RecyclerView) view.findViewById(R.id.subcategoryRecy);
        errorvolley = (TextView) view.findViewById(R.id.Error_text);
        progressBar = (ProgressBar) view.findViewById(R.id.progresBarS);
        refreshImage = (ImageView) view.findViewById(R.id.sub_cat_refresh);


        if (sub_cat_list == null) {
            makeSubCatList();
        } else {
            progressBar.setVisibility(View.GONE);
            sub_cat_adap = new Suc_Cat_Frag_Adaptor(getContext(), sub_cat_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            sub_cat_adap.setClickListener(this);
            recyclerView.setAdapter(sub_cat_adap);

        }

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
        stringRequest = new StringRequest(url, new Response.Listener<String>() {

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
                        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(AppControllerSingleton.getMinstance(), R.anim.layout_animation_fall_down);
                        sub_cat_adap = new Suc_Cat_Frag_Adaptor(getContext(), sub_cat_list);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        sub_cat_adap.setClickListener(SubCategory.this);
                        recyclerView.setAdapter(sub_cat_adap);

                        if (animation != null) {
                            recyclerView.setLayoutAnimation(animation);
                            recyclerView.scheduleLayoutAnimation();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        recyclerView.setVisibility(View.GONE);
                        errorvolley.setVisibility(View.VISIBLE);
                        errorvolley.setText(R.string.data_notfound);
                    }


                } else {
                    recyclerView.setVisibility(View.GONE);
                    errorvolley.setVisibility(View.VISIBLE);
                    refreshImage.setVisibility(View.VISIBLE);
                    errorvolley.setText("Data Not Available");
                }

                progressBar.setVisibility(View.GONE);

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

    @Override
    public void onClick(int position) {
        LyricsList obj = new LyricsList();
        Bundle bundle = new Bundle();
        bundle.putString("sub_cat_name", sub_cat_list.get(position).getText());
        bundle.putString("idsubCat", sub_cat_list.get(position).getSubid());
        bundle.putString("idCat", cat_id);
        obj.setArguments(bundle);

        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fm.replace(R.id.conta, obj);
        fm.addToBackStack(null);
        fm.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        titlefortitle = bundle.getString("title");

    }

    @Override
    public void onDetach() {
        AppControllerSingleton.getMinstance().getmRequestQueue().cancelAll(stringRequest);
        super.onDetach();
    }
}
