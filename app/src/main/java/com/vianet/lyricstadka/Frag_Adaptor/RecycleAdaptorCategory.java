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
 * Created by editing2 on 22-Nov-17.
 */

//this adaptor is used for Main Activity

public class RecycleAdaptorCategory extends RecyclerView.Adapter<RecycleAdaptorCategory.MyViewHolder> {
    private Context contextCat;
    private ArrayList<Getter_Setter> categoryList;
    private ItemClickListener clickListener;

    public RecycleAdaptorCategory(Context context, ArrayList<Getter_Setter> list) {
        this.contextCat = context;
        this.categoryList = list;

    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public RecycleAdaptorCategory.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyrics_frag_design, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(viewHolder.getAdapterPosition());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecycleAdaptorCategory.MyViewHolder holder, int position) {

        Getter_Setter getSet = categoryList.get(position);
        holder.categorynametext.setText(getSet.getText());

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView categorynametext;

        public MyViewHolder(View itemView) {
            super(itemView);
            categorynametext = (TextView) itemView.findViewById(R.id.lyrics_text);
            Typeface customFonts = Typeface.createFromAsset(contextCat.getAssets(), "fonts/gaj.ttf");
            categorynametext.setTypeface(customFonts);

        }
    }


}
