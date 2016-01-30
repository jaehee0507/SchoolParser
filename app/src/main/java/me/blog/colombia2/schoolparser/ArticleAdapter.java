package me.blog.colombia2.schoolparser;

import android.view.*;
import android.widget.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.util.Log;

import java.util.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.*;

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
        ArticleViewHolder v = new ArticleViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.card_view, p1, false));
        return v;
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
                    setContent(holder.content_text, data.get(position)[1]);
                    if(lastSelectedHolder != null)
                        lastSelectedHolder.closeHolder();
                    lastSelectedHolder = holder;
                } else {
                    holder.closeHolder();
                    lastSelectedHolder = null;
                }
            }
        });
        
        holder.scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                activity.articles.requestDisallowInterceptTouchEvent(true);
                return true;
            }
        }); 
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
    
    private void setContent(final TextView textview, final String url) {
        if(!textview.getText().toString().equals(""))
            return;
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document article = Jsoup.connect(url).timeout(60*1000).get();
                    final Elements text = article.getElementById("m_content").select("td p");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String result = "";
                            for(Element e : text)
                                result += e.text()+"\n";
                            textview.setText(result);
                        }
                    });
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
