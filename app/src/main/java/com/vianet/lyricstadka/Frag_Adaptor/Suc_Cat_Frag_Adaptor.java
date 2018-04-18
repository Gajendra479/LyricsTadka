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
import com.vianet.lyricstadka.network.ItemClickListener;

import java.util.ArrayList;

/**
 * Created by editing2 on 28-Nov-17.
 */

//this adaptor is used from SubActivity

public class Suc_Cat_Frag_Adaptor extends RecyclerView.Adapter<Suc_Cat_Frag_Adaptor.MyViewHolder> {
    private Context context;
    private ArrayList<Getter_Setter> sub_cat_list;
    private ItemClickListener clickListener;


    public Suc_Cat_Frag_Adaptor(Context context, ArrayList<Getter_Setter> list) {
        this.context = context;
        this.sub_cat_list = list;

    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public Suc_Cat_Frag_Adaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyrics_frag_design, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(holder.getAdapterPosition());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(Suc_Cat_Frag_Adaptor.MyViewHolder holder, int position) {
        Getter_Setter getter_setter = sub_cat_list.get(position);
        holder.subCateName.setText(getter_setter.getText());

    }

    @Override
    public int getItemCount() {
        return sub_cat_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subCateName;

        public MyViewHolder(View itemView) {
            super(itemView);
            subCateName = (TextView) itemView.findViewById(R.id.lyrics_text);
            Typeface customFonts = Typeface.createFromAsset(context.getAssets(), "fonts/gaj.ttf");
            subCateName.setTypeface(customFonts);
        }
    }
}
