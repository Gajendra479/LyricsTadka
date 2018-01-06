package com.vianet.lyricstadka;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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
import com.vianet.lyricstadka.Activityclass.SavedLyrics;
import com.vianet.lyricstadka.Frag_Adaptor.RecycleAdaptorCategory;
import com.vianet.lyricstadka.Frag_Adaptor.Suc_Cat_Frag_Adaptor;
import com.vianet.lyricstadka.FragmentLyrics.SubCategory;
import com.vianet.lyricstadka.network.AppControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private String url = "http://63.142.254.250/lyrics_panel/API/webservice.php?action=CategoryList";
    private ArrayList<Getter_Setter> category_name;
    private RecycleAdaptorCategory categoryAdaptor;
    private RecyclerView recyclerView;
    private TextView errorvolley;
    private ImageView refreshImage;
    ProgressBar progressBar;
    Toolbar toolbar;
    SpannableString s;
    ActionBar actionBar;
    Typeface typeface;
    private RelativeLayout conta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Typeface customFonts = Typeface.createFromAsset(getAssets(), "fonts/gaj.ttf");
        actionBar = getSupportActionBar();
        final FragmentManager fragmentManager = getSupportFragmentManager();


        s = new SpannableString("भक्ति संग्रह");
        s.setSpan(customFonts, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        if (actionBar != null) {
            actionBar.setTitle(s);
        }


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (fragmentManager.getBackStackEntryCount()>0) {

                    toggle.setDrawerIndicatorEnabled(false);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    toggle.setDrawerIndicatorEnabled(true);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        });




        //find resource id
        recyclerView = (RecyclerView) findViewById(R.id.main_recycle);
        errorvolley = (TextView) findViewById(R.id.Error_main);
        refreshImage= (ImageView) findViewById(R.id.refresh_id_image);
        progressBar = (ProgressBar) findViewById(R.id.progresBarM);
        conta = (RelativeLayout)
                findViewById(R.id.conta);

        category_name = new ArrayList<>();

        //here we call api for Sub cetogory method
        makeCategoryRequest();

        // here we handle recycle click action through activity
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getBaseContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                recyclerView.setVisibility(View.GONE);
//                conta.setVisibility(View.VISIBLE);
                Bundle bundle = new Bundle();
                bundle.putString("idcat", category_name.get(position).getId());
                bundle.putString("title",category_name.get(position).getText());
                SubCategory obj = new SubCategory();
                obj.setArguments(bundle);

                FragmentTransaction fragtans = getSupportFragmentManager().beginTransaction();
                fragtans.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.pop_exit);
                fragtans.replace(R.id.conta, obj);
                fragtans.addToBackStack(null);
                fragtans.commit();

            }

          /*  @Override
            public void onLongClick(View view, int position) {

            }*/
        }));

        refreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCategoryRequest();
                refreshImage.setVisibility(View.GONE);
                errorvolley.setVisibility(View.GONE);
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        progressBar.setVisibility(View.INVISIBLE);

    }


    // method for api call
    private void makeCategoryRequest() {

        //handle visibility of progress bar befoe getting data
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //handle if data is not awailable then it show no data available
                if (response != null && response.length() >= 0) {
                    try {
                        errorvolley.setVisibility(View.GONE);
                        refreshImage.setVisibility(View.GONE);

                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray("category");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject categoryData = jsonArray.getJSONObject(i);
                            Getter_Setter getSetObject = new Getter_Setter();
                            getSetObject.setText(categoryData.getString("cat_name"));
                            getSetObject.setId(categoryData.getString("id"));
                            category_name.add(getSetObject);
                        }
                        int resId=R.anim.layout_animation_fall_down;
                        LayoutAnimationController animation= AnimationUtils.loadLayoutAnimation(getBaseContext(),resId);
                        categoryAdaptor = new RecycleAdaptorCategory(getApplicationContext(), category_name);
                        recyclerView.setLayoutAnimation(animation);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        recyclerView.setAdapter(categoryAdaptor);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        recyclerView.setVisibility(View.GONE);
                        errorvolley.setVisibility(View.VISIBLE);
                        refreshImage.setVisibility(View.VISIBLE);
                        errorvolley.setText(R.string.data_notfound);
                    }
                } else {
                    errorvolley.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    errorvolley.setText(R.string.data_notfound);
                }

                //handle visibility of progress bar after getting data
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
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
        //here we call singleton class for network request
        AppControllerSingleton.getMinstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        //handle navigation drawer open or close and search view iconofied
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        SearchView searchView= (SearchView) findViewById(R.id.search_action);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }/* else if (!searchView.isIconified()){
            searchView.setIconified(true);
        }*/

        else {
            super.onBackPressed();
            if (getSupportFragmentManager().getBackStackEntryCount()<1){
//                toolbar.setTitle(s);
//                toolbar.setTitle("भक्ति संग्रह");
                if (actionBar != null) {
                    actionBar.setTitle(s);
                }
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        switch (item.getItemId()) {

            case R.id.nav_home:

                recyclerView.setVisibility(View.VISIBLE);

                FragmentManager fm = getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                    fm.popBackStack();
                }
                break;

            case R.id.nav_Saved_Lyrics:
                Intent s = new Intent(MainActivity.this, SavedLyrics.class);
                startActivity(s);
                break;

            case R.id.nav_share:

                shareApllication();

                break;

            case R.id.nav_rate_us:

                Uri uri = Uri.parse("market://details?id=" + getBaseContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                        | Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                );

                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException a) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName())));
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //this method is use for sharing this Application
    private void shareApllication() {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            String text = "Dear Devotees, In this app you will find All Aarti, Chalisa, Bhajan ,Mantra of all bhagwaan ji( Hindu God) in hindi font. You can share any content to your friend on a single click." + getString(R.string.app_name) + " app." + "\n\nDownload this at:";
            String link = "http://play.google.com/store/apps/details?id=" + "com.bhakti.sangrah";
            i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
            startActivity(Intent.createChooser(i, "Share link:"));
    }

    // class for handle recycler click event
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private ClickListener clicklistener;
        private GestureDetector gestureDet;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerview, final ClickListener clickListener) {
            this.clicklistener = clickListener;
            Log.d("gajendra ", "constructor");
            gestureDet = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            /*    @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerview.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerview.getChildPosition(child));
                    }
                }*/
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if (child != null && clicklistener != null && gestureDet.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildPosition(child));
            }
            Log.d("gajendra ", "onInterceptTouchEvent" + e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.d("gajendra ", "onTouchEvent" + e);

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

/*        public void onLongClick(View view, int position);*/
    }

}
