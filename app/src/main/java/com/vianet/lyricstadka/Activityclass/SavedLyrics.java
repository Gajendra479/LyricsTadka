package com.vianet.lyricstadka.Activityclass;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.vianet.lyricstadka.DataBase.DatabaseHandler;
import com.vianet.lyricstadka.Frag_Adaptor.SavedAdaptor;
import com.vianet.lyricstadka.Getter_Setter;
import com.vianet.lyricstadka.R;
import com.vianet.lyricstadka.network.AppControllerSingleton;
import com.vianet.lyricstadka.network.ItemClickListener;
import com.vianet.lyricstadka.network.RecyclerItemTouchHelper;

import java.util.ArrayList;

public class SavedLyrics extends AppCompatActivity implements ItemClickListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    ArrayList<Getter_Setter> savedlist;
    SavedAdaptor savedAdaptor;
    DatabaseHandler helper;
    CoordinatorLayout coordinatorLayout;
    android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_lyrics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Favorite Lyrics");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.saved_recycle_view);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);

        helper = new DatabaseHandler(getBaseContext());

        savedlist = helper.selectData();
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(AppControllerSingleton.getMinstance(), R.anim.layout_animation_fall_down);

        savedAdaptor = new SavedAdaptor(this, savedlist, getSupportFragmentManager());
        if (animation != null) {
            recyclerView.setLayoutAnimation(animation);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(savedAdaptor);
        savedAdaptor.setClickListener(this);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

      /*  ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // view the background view
            }
        };

// attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);*/
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
            actionBar.setTitle("Favorite Lyrics");
            onBackPressed();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(int position) {
        helper.deleteDataFromLyrics(savedlist.get(position).getText());
        savedlist.remove(position);
        savedAdaptor.notifyDataSetChanged();
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, final int position) {

        if (viewHolder instanceof SavedAdaptor.MyViewHolder) {
            // get the removed item name to display it in snack bar

            helper.deleteDataFromLyrics(savedlist.get(viewHolder.getAdapterPosition()).getText());

            String name = savedlist.get(viewHolder.getAdapterPosition()).getText();
            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from favorite list!", Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            savedlist.remove(position);
            savedAdaptor.notifyDataSetChanged();

         /*   if (direction==ItemTouchHelper.LEFT){
                AlertDialog.Builder builder = new AlertDialog.Builder(SavedLyrics.this); //alert for confirm to delete
                builder.setMessage("Are you sure to delete?");    //set message

                builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


            return;

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        savedAdaptor.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                        savedAdaptor.notifyItemRangeChanged(position, savedAdaptor.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                        return;

                    }
                }).show();

            }*/

        }

    }
}
