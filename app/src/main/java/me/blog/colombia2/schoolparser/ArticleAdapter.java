package me.blog.colombia2.schoolparser;

import android.view.*;
import android.widget.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;

import java.util.*;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
    private MainActivity activity;
    private ArrayList<String[]> data;
    private ArrayList<ArrayList<String[]>> files;
    private ArticleViewHolder lastSelectedHolder;
    
    public ArticleAdapter(MainActivity activity, ArrayList<String[]> data, ArrayList<ArrayList<String[]>> files) {
        this.activity = activity;
        this.data = data;
        this.files = files;
        this.lastSelectedHolder = null;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
        return new ArticleViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.card_view, p1, false));
    }

    @Override
    public void onBindViewHolder(final ArticleViewHolder holder, final int position) {
        holder.titleText.setText(this.data.get(position)[0]);
        holder.dateText.setText(this.data.get(position)[2]);
        
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!holder.selected) {
                    holder.openHolder();
                    if(lastSelectedHolder != null)
                        lastSelectedHolder.closeHolder();
                    lastSelectedHolder = holder;
                } else {
                    holder.closeHolder();
                    lastSelectedHolder = null;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
