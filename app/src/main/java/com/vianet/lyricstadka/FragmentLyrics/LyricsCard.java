package com.vianet.lyricstadka.FragmentLyrics;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.vianet.lyricstadka.DataBase.DatabaseHandler;
import com.vianet.lyricstadka.Getter_Setter;
import com.vianet.lyricstadka.R;
import com.vianet.lyricstadka.network.AppControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import static android.support.v7.content.res.AppCompatResources.getDrawable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LyricsCard.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LyricsCard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LyricsCard extends Fragment {

    private int[] dayImageArray = new int[]{R.drawable.dayfive, R.drawable.dayfour, R.drawable.daythree, R.drawable.daytwo, R.drawable.dayone};

    private int[] nightImageArray = new int[]{R.drawable.nightone, R.drawable.nighttwo, R.drawable.nightthree, R.drawable.nightfour, R.drawable.nightfive};
    private OnFragmentInteractionListener mListener;
    private String title;
    private TextView titlehead;
    private String description;
    public Context context;
    private String url;
    private ScrollView scrollView;
    private TextView errorvolley;
    private FloatingActionButton fab, fabSaved;
    boolean nightboolean;
    private String sub_cat_name;
    private ProgressBar progressBar;
    private DatabaseHandler helperDataBase;
    private TextView semicolon;
    private RelativeLayout rootview;
    private ImageView refreshImage;
    private Random r;
    private View view1;
    private ArrayList<Getter_Setter> savedCard = new ArrayList<>();
    private TextView title_name, descriptiom_name;
    private ScaleGestureDetector sgd;
    private float txtSize;
    private String titlefortitle;


    public LyricsCard() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LyricsCard newInstance(String param1, String param2) {
        LyricsCard fragment = new LyricsCard();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_lyrics_card, container, false);


        rootview = (RelativeLayout) view.findViewById(R.id.card_lyrics_root);

        fab = (FloatingActionButton) view.findViewById(R.id.floatingNightDay);
        fabSaved = (FloatingActionButton) view.findViewById(R.id.savedLyrics);
        semicolon = (TextView) view.findViewById(R.id.semicolon_id);
        refreshImage = (ImageView) view.findViewById(R.id.lyrics_card_refresh);
        view1 = view.findViewById(R.id.viewlineidcard);
        title_name = (TextView) view.findViewById(R.id.lyricsSongName);
        descriptiom_name = (TextView) view.findViewById(R.id.lyricsCard_Lyrics_Description);
        txtSize = descriptiom_name.getTextSize();
        errorvolley = (TextView) view.findViewById(R.id.cardtexterror);
        scrollView = (ScrollView) view.findViewById(R.id.lyrics_Card_Scroll);
        progressBar = (ProgressBar) view.findViewById(R.id.progresBarC);
        titlehead = (TextView) view.findViewById(R.id.title_head);


        titlehead.setTextColor(Color.parseColor("#fffefe"));
        scrollView.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        fabSaved.setVisibility(View.GONE);

        final Bundle bundle = getArguments();
        url = bundle.getString("id");
        sub_cat_name = bundle.getString("sub_cat_name1");


        r = new Random();


        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (!nightboolean) {
                    fab.setImageResource(R.drawable.night_imagen);
                    Integer image = nightImageArray[r.nextInt(nightImageArray.length)];
                    rootview.setBackgroundResource(image);
                    view1.setBackgroundColor(Color.parseColor("#FF000000"));
                    semicolon.setTextColor(Color.parseColor("#FF000000"));
                    titlehead.setTextColor(Color.parseColor("#FF000000"));
                    fab.setBackgroundColor(Color.parseColor("#e25750"));
                    title_name.setTextColor(Color.parseColor("#FF000000"));
                    descriptiom_name.setTextColor(Color.parseColor("#FF000000"));
                    nightboolean = true;

                } else {

                    Integer image = dayImageArray[r.nextInt(dayImageArray.length)];
                    rootview.setBackgroundResource(image);
                    fab.setImageResource(R.drawable.ic_day_nig);
                    view1.setBackgroundColor(Color.parseColor("#fffefe"));
                    semicolon.setTextColor(Color.parseColor("#fffefe"));
                    title_name.setTextColor(Color.parseColor("#fffefe"));
                    titlehead.setTextColor(Color.parseColor("#fffefe"));
                    descriptiom_name.setTextColor(Color.parseColor("#fffefe"));
                    nightboolean = false;

                }

            }
        });


        if (url != null) {
            //this method call from LyricsList becouse we are getting url from LyricsList
            makeCardLyricsrequest(url);
        } else {
            //this method is call from SavedLyrics Activity
            setDataFromSavedActivity();
        }


        //this fab button is used for saving data to Sqlite database
        fabSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fabSaved.setEnabled(false);

                if (fabSaved.getDrawable().getConstantState() == getDrawable(getContext(), R.drawable.ic_saved_data)
                        .getConstantState()) {

                    helperDataBase.insertLyrics(description, title, sub_cat_name);
                    fabSaved.setImageResource(R.drawable.ic_saved_alredy);
                    Toast.makeText(getContext(), "ADD FAVORITE", Toast.LENGTH_SHORT).show();
                } else {
                    fabSaved.setImageResource(R.drawable.ic_saved_data);

                    if (savedCard != null) {
                        Toast.makeText(getContext(), "REMOVE FAVORITE", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < savedCard.size(); i++) {
                            if (title.equals(savedCard.get(i).getText())) {

                                helperDataBase.deleteDataFromLyrics(savedCard.get(i).getText());

                                break;
                            }
                        }
                    }

                }
                fabSaved.setEnabled(true);
            }
        });

        refreshImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCardLyricsrequest(url);
                refreshImage.setVisibility(View.GONE);
                errorvolley.setVisibility(View.GONE);
            }
        });

        sgd = new ScaleGestureDetector(getContext(), new simpleOnScaleGestureListener());
        descriptiom_name.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    //stuff for 1 pointer
                } else { //when 2 pointers are present
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            sgd.onTouchEvent(event);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            sgd.onTouchEvent(event);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return true;
            }
        });

        return view;
    }

    //later in the code
    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float size = descriptiom_name.getTextSize();

            float factor = detector.getScaleFactor();

            int increase = 0;

            if (factor > 1.0f)
                increase = 2;
            else if (factor < 1.0f)
                increase = -2;

            size += increase;

            if (txtSize <= size)
                descriptiom_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    }


    //this method is call from SavedLyrics Activity becouse we use same fragment from LyricsList and SavedLyrics activity
    private void setDataFromSavedActivity() {
        progressBar.setVisibility(View.GONE);
        errorvolley.setVisibility(View.GONE);
        rootview.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);

        Integer image = dayImageArray[r.nextInt(dayImageArray.length)];
        rootview.setBackgroundResource(image);

        // here we get data from SavedLyrics for showing data on this frag
        final Bundle bundle = getArguments();

        String text = bundle.getString("text");
        String description = bundle.getString("description");
        String titlebundle = bundle.getString("title");
/*
        Log.d("Lyrics database bundle", text);
        Log.d("Lyrics database bundle", description);
        Log.d("Lyrics database bundle", titlebundle);*/

        title_name.setText(text);
        descriptiom_name.setText(description);
        titlehead.setText(titlebundle);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.card_lrics_saved, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_share_lyrics_saved) {
            Intent sharetext = new Intent(Intent.ACTION_SEND);
            sharetext.setType("text/plain");
            String text = "Dear Devotees, In this one app you can find All Aarti, Chalisa, Bhajan ,Mantra of all bhagwaanji( Hindu God) in hindi font. You can share any content to your friend on a single click." + getString(R.string.app_name) + " app ." + "\nDownload this at:";
            String link = "http://play.google.com/store/apps/details?id=" + "com.vianet.BhaktiSangreh";
            sharetext.putExtra(Intent.EXTRA_TEXT, String.valueOf(description) + "\n\n" + text + link);
            startActivity(Intent.createChooser(sharetext, "sahre via"));
        }
        return super.onOptionsItemSelected(item);
    }

    //this method is call when url is available from Lyrics list
    private void makeCardLyricsrequest(String url) {


        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringreq = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    errorvolley.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    fabSaved.setVisibility(View.VISIBLE);

                    JSONObject object = new JSONObject(response);
                    JSONArray jsonarray = object.getJSONArray("LyricsRead");
                    JSONObject lyricsContent = jsonarray.getJSONObject(0);
                    title = lyricsContent.getString("title");
                    description = lyricsContent.getString("desc1");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        description = String.valueOf(Html.fromHtml(lyricsContent.getString("desc1"), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        description = String.valueOf(Html.fromHtml(description));
                    }

                    Integer image = dayImageArray[r.nextInt(dayImageArray.length)];
                    rootview.setBackgroundResource(image);
                    title_name.setText(title);
                    descriptiom_name.setText(description);
                    titlehead.setText(sub_cat_name);


                } catch (JSONException e) {
                    e.printStackTrace();
                    errorvolley.setVisibility(View.VISIBLE);
                    refreshImage.setVisibility(View.VISIBLE);
                    errorvolley.setText(R.string.data_notfound);
                }

                progressBar.setVisibility(View.GONE);

                fabImgChange();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                fabSaved.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
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

/*    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/


        @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            Bundle bundle = getArguments();
            titlefortitle = bundle.getString("lyrics");
        /*if (context instanceof OnFragmentInteractionListener) {
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

    public void fabImgChange() {
        helperDataBase = new DatabaseHandler(getContext());
        savedCard = helperDataBase.selectData();
        boolean flag = true;
        if (savedCard != null && title != null) {
            for (int i = 0; i < savedCard.size(); i++) {
                if (title.equals(savedCard.get(i).getText())) {
                    fabSaved.setImageResource(R.drawable.ic_saved_alredy);
                    flag = false;
                    break;
                }
            }
            if (flag) {
                fabSaved.setImageResource(R.drawable.ic_saved_data);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        fabImgChange();
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
