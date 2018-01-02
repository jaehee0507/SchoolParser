package me.blog.colombia2.schoolparser;

import android.support.design.widget.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

public class LoadMoreHolder extends RecyclerView.ViewHolder {
    public FloatingActionButton loadMore;
    public ProgressBar loading;
    
    public LoadMoreHolder(View itemView) {
        super(itemView);
        
        loadMore = (FloatingActionButton) itemView.findViewById(R.id.fab);
        loading  = (ProgressBar) itemView.findViewById(R.id.loading);
    }
}
