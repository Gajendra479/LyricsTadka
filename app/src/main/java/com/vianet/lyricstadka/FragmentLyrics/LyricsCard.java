package com.vianet.lyricstadka.FragmentLyrics;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.vianet.lyricstadka.DataBase.DatabaseHandler;
import com.vianet.lyricstadka.Getter_Setter;
import com.vianet.lyricstadka.R;
import com.vianet.lyricstadka.network.AppControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.support.v7.content.res.AppCompatResources.getDrawable;

public class LyricsCard extends Fragment implements GestureDetector.OnGestureListener {

    public Context context;
    boolean nightboolean;
    private int[] dayImageArray = new int[]{R.drawable.dayfive, R.drawable.dayfour, R.drawable.daythree, R.drawable.daytwo, R.drawable.dayone};
    private int[] nightImageArray = new int[]{R.drawable.nightone, R.drawable.nighttwo, R.drawable.nightthree, R.drawable.nightfour, R.drawable.nightfive};
    private String title;
    private TextView titlehead;
    private String description;
    private String url;
    private ScrollView scrollView;
    private TextView errorvolley;
    private FloatingActionButton fab, fabSaved;
    private String sub_cat_name;
    private ProgressBar progressBar;
    private DatabaseHandler helperDataBase;
    private TextView semicolon;
    private RelativeLayout rootview;
    private ImageView refreshImage;
    private Random r;
    private View view1;
    private ArrayList<Getter_Setter> savedCard = new ArrayList<>();
    private TextView title_name, description_name;
    private ScaleGestureDetector sgd;
    private float txtSize;
    private String titlefortitle;
    private ClipboardManager myClipboard;
    private ClipData myClip;


    public LyricsCard() {
        // Required empty public constructor
    }

    /* // TODO: Rename and change types and number of parameters
     public static LyricsCard newInstance(String param1, String param2) {
         return new LyricsCard();
     }
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        titlefortitle = bundle.getString("lyrics");

    }

    @SuppressLint("ClickableViewAccessibility")
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
        description_name = (TextView) view.findViewById(R.id.lyricsCard_Lyrics_Description);
        txtSize = description_name.getTextSize();
        errorvolley = (TextView) view.findViewById(R.id.cardtexterror);
        scrollView = (ScrollView) view.findViewById(R.id.lyrics_Card_Scroll);
        progressBar = (ProgressBar) view.findViewById(R.id.progresBarC);
        titlehead = (TextView) view.findViewById(R.id.title_head);
        titlehead.setTextColor(Color.parseColor("#fffefe"));
        scrollView.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        fabSaved.setVisibility(View.GONE);

//        fabSaved.setVisibility(View.GONE);

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
                    description_name.setTextColor(Color.parseColor("#FF000000"));
                    nightboolean = true;

                } else {

                    Integer image = dayImageArray[r.nextInt(dayImageArray.length)];
                    rootview.setBackgroundResource(image);
                    fab.setImageResource(R.drawable.ic_day_nig);
                    view1.setBackgroundColor(Color.parseColor("#fffefe"));
                    semicolon.setTextColor(Color.parseColor("#fffefe"));
                    title_name.setTextColor(Color.parseColor("#fffefe"));
                    titlehead.setTextColor(Color.parseColor("#fffefe"));
                    description_name.setTextColor(Color.parseColor("#fffefe"));
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

                    savedCard = helperDataBase.selectData();
                    if (savedCard != null && title != null) {
                        for (int i = 0; i < savedCard.size(); i++) {
                            if (title.equals(savedCard.get(i).getText())) {
                                helperDataBase.deleteDataFromLyrics(savedCard.get(i).getText());
                                fabSaved.setImageResource(R.drawable.ic_saved_data);
                                Toast.makeText(getContext(), "REMOVE FAVORITE", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
                fabSaved.setEnabled(true);
            }
        });

        refreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeCardLyricsrequest(url);
                refreshImage.setVisibility(View.GONE);
                errorvolley.setVisibility(View.GONE);

            }
        });


        sgd = new ScaleGestureDetector(getContext(), new simpleOnScaleGestureListener());

        description_name.setOnTouchListener(new View.OnTouchListener() {

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
                return false;
            }
        });

        return view;
    }

    public void fabImgChange() {

        helperDataBase = new DatabaseHandler(AppControllerSingleton.getMinstance());

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
        title = bundle.getString("text");
        description = bundle.getString("description");

        titlehead.setText(title);
        description_name.setText(description);
        description_name.setTextIsSelectable(true);
        title_name.setText(titlefortitle);

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
            sharetext.putExtra(Intent.EXTRA_TEXT, titlefortitle + "\n" + description + "\n" + "\n" + getString(R.string.app_link));
            startActivity(Intent.createChooser(sharetext, "share via"));
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

                    JSONArray jsonarray = new JSONObject(response).getJSONArray("LyricsRead");
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
                    description_name.setText(description);
                    description_name.setTextIsSelectable(true);
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

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(titlefortitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /*  @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        *//*Bundle bundle = getArguments();

        titlefortitle = bundle.getString("lyrics");*//*

    }*/


    @Override
    public void onStart() {
        super.onStart();
        fabImgChange();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

        myClipboard = (ClipboardManager) AppControllerSingleton.getMinstance().getSystemService(CLIPBOARD_SERVICE);

        description_name.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {

                    case android.R.id.copy:

                        int min = 0;
                        int max = description_name.getText().length();
                        if (description_name.isFocused()) {

                            final int selStart = description_name.getSelectionStart();
                            final int selEnd = description_name.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));

                        }
                        // Perform your definition lookup with the selected text
                        final CharSequence selectedText = description_name.getText()
                                .subSequence(min, max);
                        String text = selectedText.toString();

                        myClip = ClipData.newPlainText("text", text);

                        myClipboard.setPrimaryClip(myClip);

                        // Finish and close the ActionMode
                        mode.finish();
                        return true;
                    case android.R.id.cut:
                        // add your custom code to get cut functionality according
                        // to your requirement
                        return true;
                    case android.R.id.paste:
                        // add your custom code to get paste functionality according
                        // to your requirement
                        return true;

                    default:
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });


    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
/*
    @Override
    public void onDetach() {
        super.onDetach();
    }*/

    //later in the code
    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float size = description_name.getTextSize();

            float factor = detector.getScaleFactor();

            int increase = 0;

            if (factor > 1.0f)
                increase = 2;
            else if (factor < 1.0f)
                increase = -2;

            size += increase;

            if (txtSize <= size)
                description_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
