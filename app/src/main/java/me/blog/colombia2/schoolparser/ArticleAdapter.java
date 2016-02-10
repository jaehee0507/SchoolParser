package me.blog.colombia2.schoolparser;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.wang.avi.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import me.blog.colombia2.schoolparser.parser.*;
import junit.framework.*;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
    protected MainActivity activity;
    protected ArrayList<ArticleData> articleData;
    protected ArticleViewHolder lastSelectedHolder;
    
    public ArticleAdapter(MainActivity activity, ArrayList<ArticleData> articleData) {
        this.activity = activity;
        this.articleData = articleData;
        this.lastSelectedHolder = null;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
        return new ArticleViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.card_view, p1, false));
    }

    @Override
    public void onBindViewHolder(final ArticleViewHolder holder, final int position) {
        if(position == articleData.size()-1) {
            int fivedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, activity.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(fivedp, fivedp, fivedp, fivedp);
            holder.card.setLayoutParams(params);
        }
        
        final ArticleData article = articleData.get(position);
        
        holder.titleText.setText(article.getTitle());
        holder.dateText.setText(article.getDate());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.refresh.isRefreshing())
                    return;
                
                if(!holder.selected) {
                    holder.openHolder();
                    setContent(holder.loading, holder.content_text, article.getHyperLink());
                    if(lastSelectedHolder != null)
                        lastSelectedHolder.closeHolder();
                    lastSelectedHolder = holder;
                } else {
                    holder.closeHolder();
                    lastSelectedHolder = null;
                }
            }
        });
        
        holder.content_gotourl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getHyperLink()));
                activity.startActivity(i);
            }
        });
        
        holder.content_attachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeActivity(article.getAttachments());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.articleData.size();
    }
    
    private void setContent(final AVLoadingIndicatorView loading, final TextView textview, final String url) {
        if(loading.getVisibility() == View.GONE)
            return;
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document article = Jsoup.connect(url).timeout(60*1000).get();
                    final Elements text = (article.getElementById("m_content").select("td p").size() > 0 ? article.getElementById("m_content").select("td p") : article.getElementById("m_content").select("td"));
                    
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String result = "";
                            for(Element e : text)
                                result += e.text()+"\n";
                            textview.setText(result);
                            loading.setVisibility(View.GONE);
                        }
                    });
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
