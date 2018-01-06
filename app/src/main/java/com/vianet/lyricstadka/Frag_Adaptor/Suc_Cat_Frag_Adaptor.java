package com.vianet.lyricstadka.Frag_Adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vianet.lyricstadka.Getter_Setter;
import com.vianet.lyricstadka.R;

import java.util.ArrayList;

/**
 * Created by editing2 on 28-Nov-17.
 */

//this adaptor is used from SubActivity

public class Suc_Cat_Frag_Adaptor extends RecyclerView.Adapter<Suc_Cat_Frag_Adaptor.MyViewHolder> {
    private Context context;
    private ArrayList<Getter_Setter> sub_cat_list;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subCateName;
        public MyViewHolder(View itemView) {
            super(itemView);
            subCateName= (TextView) itemView.findViewById(R.id.sub_cat_text);
            Typeface customFonts = Typeface.createFromAsset(context.getAssets(), "fonts/gaj.ttf");
            subCateName.setTypeface(customFonts);
        }
    }

    public Suc_Cat_Frag_Adaptor(Context context,ArrayList<Getter_Setter> list){
        this.context=context;
        this.sub_cat_list=list;

    }

    @Override
    public Suc_Cat_Frag_Adaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_cat_design,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Suc_Cat_Frag_Adaptor.MyViewHolder holder, int position) {
        Getter_Setter getter_setter=sub_cat_list.get(position);
        holder.subCateName.setText(getter_setter.getText());

    }

    @Override
    public int getItemCount() {
        return sub_cat_list.size();
    }
}
