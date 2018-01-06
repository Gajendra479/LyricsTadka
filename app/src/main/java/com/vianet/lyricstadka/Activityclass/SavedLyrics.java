package com.vianet.lyricstadka.Activityclass;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.vianet.lyricstadka.DataBase.DatabaseHandler;
import com.vianet.lyricstadka.Frag_Adaptor.RecycleAdaptorCategory;
import com.vianet.lyricstadka.Frag_Adaptor.SavedAdaptor;
import com.vianet.lyricstadka.Getter_Setter;
import com.vianet.lyricstadka.R;
import java.util.ArrayList;

public class SavedLyrics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_lyrics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Favorite Lyrics");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.saved_recycle_view);
        ArrayList<Getter_Setter> savedlist = new ArrayList<Getter_Setter>();
        DatabaseHandler helper = new DatabaseHandler(getBaseContext());

        savedlist = helper.selectData();
        int resId=R.anim.layout_animation_fall_down;
        LayoutAnimationController animation= AnimationUtils.loadLayoutAnimation(getBaseContext(),resId);
        SavedAdaptor savedAdaptor = new SavedAdaptor(this, savedlist, getSupportFragmentManager());
        recyclerView.setLayoutAnimation(animation);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(savedAdaptor);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_saved_lyrics, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        final int home = android.R.id.home;
        if (id == home) {
            onBackPressed();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
