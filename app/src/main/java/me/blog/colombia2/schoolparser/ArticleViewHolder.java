package me.blog.colombia2.schoolparser;

import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import me.blog.colombia2.schoolparser.utils.*;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    public TextView titleText;
    public TextView dateText;
    public LinearLayout content;
    public ProgressBar loading;
    public TextView content_text;
    public Button content_readall;
    public Button content_attachments;
    public Button content_replies;
    public CardView card;
    public boolean opened;
    
    public ArticleViewHolder(View itemView) {
        super(itemView);
        titleText = (TextView) itemView.findViewById(R.id.title);
        dateText  = (TextView) itemView.findViewById(R.id.date);
        content   = (LinearLayout) itemView.findViewById(R.id.content);
        card      = (CardView) itemView;
        opened    = false;
        
        content_text        = (TextView) content.findViewById(R.id.content_text);
        content_readall     = (Button) content.findViewById(R.id.readall);
        content_attachments = (Button) content.findViewById(R.id.attachments);
        content_replies     = (Button) content.findViewById(R.id.replies);
        
        loading = (ProgressBar) itemView.findViewById(R.id.loading);
    }
    
    public void openHolder(boolean anima) {
        int target = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, content.getContext().getResources().getDisplayMetrics());
        ResizeAnimation anim = new ResizeAnimation(content, -1, target);
        anim.setDuration(anima ? 300 : 0);
        content.startAnimation(anim);
        opened = true;
    }
    
    public void closeHolder(boolean anima) {
        int target = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, content.getContext().getResources().getDisplayMetrics());
        ResizeAnimation anim = new ResizeAnimation(content, -1, target);
        anim.setDuration(anima ? 300 : 0);
        content.startAnimation(anim);
        opened = false;
    }
    
    public boolean isOpened() {
        return opened;
    }
}
