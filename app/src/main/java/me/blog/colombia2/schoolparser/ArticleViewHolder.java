package me.blog.colombia2.schoolparser;

import android.view.*;
import android.widget.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.animation.*;

import com.wang.avi.AVLoadingIndicatorView;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    protected TextView titleText;
    protected TextView dateText;
    protected LinearLayout content;
    protected AVLoadingIndicatorView loading;
    protected TextView content_text;
    protected Button content_gotourl;
    protected Button content_attachments;
    protected CardView card;
    protected boolean selected;
    
    public ArticleViewHolder(View itemView) {
        super(itemView);
        titleText = (TextView) itemView.findViewById(R.id.title);
        dateText  = (TextView) itemView.findViewById(R.id.date);
        content   = (LinearLayout) itemView.findViewById(R.id.content);
        card      = (CardView) itemView;
        selected  = false;
        
        content_text        = (TextView) content.findViewById(R.id.content_text);
        content_gotourl     = (Button) content.findViewById(R.id.gotourl);
        content_attachments = (Button) content.findViewById(R.id.attachments);
        
        loading = (AVLoadingIndicatorView) itemView.findViewById(R.id.loading);
    }
    
    public void openHolder() {
        int target = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, content.getContext().getResources().getDisplayMetrics());
        ResizeAnimation anim = new ResizeAnimation(content, -1, target);
        anim.setDuration(300);
        content.startAnimation(anim);
        selected = true;
    }
    
    public void closeHolder() {
        int target = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, content.getContext().getResources().getDisplayMetrics());
        ResizeAnimation anim = new ResizeAnimation(content, -1, target);
        anim.setDuration(300);
        content.startAnimation(anim);
        selected = false;
    }
}
