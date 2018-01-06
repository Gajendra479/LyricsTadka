package com.vianet.lyricstadka.Frag_Adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vianet.lyricstadka.Getter_Setter;
import com.vianet.lyricstadka.R;

import java.util.ArrayList;

/**
 * Created by editing2 on 29-Nov-17.
 */


//this adaptor is used for LyricsCard Fragment

public class LyricsAdaptor extends RecyclerView.Adapter<LyricsAdaptor.MyViewHolder> {
    private Context context;
    public ArrayList<Getter_Setter> lyricsList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView lyricsText;
        public MyViewHolder(View itemView) {
            super(itemView);
            lyricsText= (TextView) itemView.findViewById(R.id.lyrics_text);
            Typeface customFonts = Typeface.createFromAsset(context.getAssets(), "fonts/gaj.ttf");
            lyricsText.setTypeface(customFonts);
        }
    }

    public LyricsAdaptor(Context context , ArrayList<Getter_Setter> lyricsList){
        this.context=context;
        this.lyricsList=lyricsList;

    }

    @Override
    public LyricsAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyrics_frag_design,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LyricsAdaptor.MyViewHolder holder, int position) {
        Getter_Setter getter_setter=lyricsList.get(position);
        holder.lyricsText.setText(getter_setter.getText());

    }

    @Override
    public int getItemCount() {
        return lyricsList.size();
    }
}
