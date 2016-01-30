package me.blog.colombia2.schoolparser;

import android.view.*;
import android.widget.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    protected TextView titleText;
    protected TextView dateText;
    protected CardView card;
    protected boolean selected;
    
    public ArticleViewHolder(View itemView) {
        super(itemView);
        titleText = (TextView) itemView.findViewById(R.id.title);
        dateText  = (TextView) itemView.findViewById(R.id.date);
        card      = (CardView) itemView;
        selected  = false;
    }
    
    public void openHolder() {
        ResizeAnimation anim = new ResizeAnimation(card, 2f);
        anim.setDuration(300);
        card.startAnimation(anim);
        selected = true;
    }
    
    public void closeHolder() {
        ResizeAnimation anim = new ResizeAnimation(card, 0.5f);
        anim.setDuration(300);
        card.startAnimation(anim);
        selected = false;
    }
}
