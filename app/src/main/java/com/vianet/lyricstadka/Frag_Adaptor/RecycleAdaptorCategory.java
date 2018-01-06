package com.vianet.lyricstadka.Frag_Adaptor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vianet.lyricstadka.Getter_Setter;
import com.vianet.lyricstadka.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.category;
import static android.R.attr.thickness;
import static android.R.attr.typeface;

/**
 * Created by editing2 on 22-Nov-17.
 */

//this adaptor is used for Main Activity

public class RecycleAdaptorCategory extends RecyclerView.Adapter<RecycleAdaptorCategory.MyViewHolder>{
    private View cardview;
    private Context contextCat;
    private ArrayList<Getter_Setter> categoryList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView categorynametext;

        public MyViewHolder(View itemView) {
            super(itemView);
            categorynametext= (TextView) itemView.findViewById(R.id.nametextextra);
            cardview=itemView.findViewById(R.id.cardviewmain);
            Typeface customFonts = Typeface.createFromAsset(contextCat.getAssets(), "fonts/gaj.ttf");
            categorynametext.setTypeface(customFonts);

        }
    }

    public RecycleAdaptorCategory(Context context,ArrayList<Getter_Setter> list){
        this.contextCat=context;
        this.categoryList=list;

    }

    @Override
    public RecycleAdaptorCategory.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.extracode,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecycleAdaptorCategory.MyViewHolder holder, int position) {

        Getter_Setter getSet=categoryList.get(position);
        holder.categorynametext.setText(getSet.getText());

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


}
