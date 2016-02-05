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
        if(position == data.size()-1) {
            int fivedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, activity.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(fivedp, fivedp, fivedp, fivedp);
            holder.card.setLayoutParams(params);
        }
        String title = this.data.get(position)[0];
        if(this.data.get(position)[3].equals("1")) {
            title += " N";
            SpannableStringBuilder builder = new SpannableStringBuilder(title);
            Drawable icon = activity.getResources().getDrawable(R.drawable.newicon);
            icon.setBounds(0, 0, holder.titleText.getLineHeight(), holder.titleText.getLineHeight());
            builder.setSpan(new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM) {
                //From http://stackoverflow.com/a/31491580
                public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                    Drawable b = getDrawable();
                    canvas.save();
                    
                    int transY = bottom - b.getBounds().bottom;
                    transY -= paint.getFontMetricsInt().descent / 2;

                    canvas.translate(x, transY);
                    b.draw(canvas);
                    canvas.restore();
                }
            }, title.length()-1, title.length(), SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE);
            holder.titleText.setText(builder);
        } else
            holder.titleText.setText(title);
        holder.dateText.setText(this.data.get(position)[2]);
        
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.refresh.isRefreshing())
                    return;
                
                if(!holder.selected) {
                    holder.openHolder();
                    setContent(holder.loading, holder.content_text, data.get(position)[1]);
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
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(position)[1]));
                activity.startActivity(i);
            }
        });
        
        holder.content_attachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeActivity(files.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
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
