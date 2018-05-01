package com.vianet.lyricstadka.Frag_Adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vianet.lyricstadka.DataBase.DatabaseHandler;
import com.vianet.lyricstadka.FragmentLyrics.LyricsCard;
import com.vianet.lyricstadka.Getter_Setter;
import com.vianet.lyricstadka.R;
import com.vianet.lyricstadka.network.ItemClickListener;

import java.util.ArrayList;

/**
 * Created by editing2 on 05-Dec-17.
 */

//this adaptor is used for SavedLyrics Activity

public class SavedAdaptor extends RecyclerView.Adapter<SavedAdaptor.MyViewHolder> {
    private Context context;
    private ArrayList<Getter_Setter> savedLyrics;

    private FragmentManager fragmentManager;
//    private ItemClickListener clickListener;


    public SavedAdaptor(Context context, ArrayList<Getter_Setter> savedlist, FragmentManager fragmentManager) {

        this.context = context;
        this.savedLyrics = savedlist;
        this.fragmentManager = fragmentManager;
//        DatabaseHandler helper = new DatabaseHandler(context);

    }

  /*  public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_design_lyrics, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Getter_Setter getter_setter = savedLyrics.get(position);
        holder.savedLyricsText.setText(getter_setter.getText());
       /* holder.delete_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null){
                    clickListener.onClick(holder.getAdapterPosition());
                }
            }
        });*/

        holder.savedLyricsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LyricsCard obj = new LyricsCard();
                Bundle bundle1 = new Bundle();
                bundle1.putString("description", savedLyrics.get(holder.getAdapterPosition()).getDescription());
                bundle1.putString("lyrics", savedLyrics.get(holder.getAdapterPosition()).getText());
                bundle1.putString("text", savedLyrics.get(holder.getAdapterPosition()).getHead());
                obj.setArguments(bundle1);

                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.sacvedContainer, obj);
                ft.addToBackStack(null);
                ft.commit();

            }
        });

    }
/*
    public void removeItem(int position) {
         savedLyrics.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Getter_Setter item, int position) {
        savedLyrics.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }*/

    @Override
    public int getItemCount() {
        return savedLyrics.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout viewForground, viewBackground;
//        ImageView delete_icon;
        private TextView savedLyricsText;

        public MyViewHolder(View itemView) {
            super(itemView);
            savedLyricsText = itemView.findViewById(R.id.saved_Lyrics_text);
//            delete_icon= (ImageView) itemView.findViewById(R.id.delete_icon);
            viewForground = itemView.findViewById(R.id.view_foreground);
            viewBackground = itemView.findViewById(R.id.view_background);
            Typeface customFonts = Typeface.createFromAsset(context.getAssets(), "fonts/gaj.ttf");
            savedLyricsText.setTypeface(customFonts);
        }
    }
}
