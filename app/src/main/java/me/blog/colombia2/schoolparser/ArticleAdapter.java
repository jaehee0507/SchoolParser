package me.blog.colombia2.schoolparser;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected MainActivity activity;
    protected ArrayList<ArticleData> articleData;
    protected boolean loading;
    
    public ArticleAdapter(MainActivity activity, ArrayList<ArticleData> articleData) {
        this.activity = activity;
        this.articleData = articleData;
        this.loading = false;
    }

    @Override
    public int getItemViewType(int position) {
        return articleData.get(position) != null ? 0 : 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup p1, int view_type) {
        RecyclerView.ViewHolder vh;
        if(view_type == 0)
            vh = new ArticleViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.card_view, p1, false));
        else
            vh = new LoadMoreHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.load_more, p1, false));
        
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder a, final int position) {
        final ArticleData article = articleData.get(position);
        
        if(a instanceof ArticleViewHolder) {
            final ArticleViewHolder holder = (ArticleViewHolder) a;
            holder.closeHolder(false);
            holder.loading.setVisibility(View.VISIBLE);
            holder.content_text.setText("");
            holder.opened = false;
                
            holder.titleText.setText(article.getTitle());
            holder.dateText.setText(article.getDate());
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(activity.refresh.isRefreshing())
                        return;
                    
                    if(holder.isOpened())
                        holder.closeHolder(true);
                    else {
                        holder.openHolder(true);
                        setContent(holder, article.getHyperLink());
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
        } else if(a instanceof LoadMoreHolder) {
            final LoadMoreHolder holder = (LoadMoreHolder) a;
            
            if(loading) {
                holder.loadMore.setVisibility(View.INVISIBLE);
                holder.loading.setVisibility(View.VISIBLE);
            } else {
                holder.loading.setVisibility(View.INVISIBLE);
                holder.loadMore.setVisibility(View.VISIBLE);
            }
            
            holder.loadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loading = true;
                    holder.loadMore.setVisibility(View.INVISIBLE);
                    holder.loading.setVisibility(View.VISIBLE);
                    
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final int orgin_size = articleData.size();
                            try {
                                ListParser parser = ListParser.getInstance();
                                parser.setCurrentPage(parser.getCurrentPage() + 1)
                                      .connect();
                                final ArrayList<ArticleData> arr = parser.getArticleList();
                                articleData.addAll(arr);
                                
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyItemRangeInserted(orgin_size, arr.size());
                                        loading = false;
                                        articleData.remove(orgin_size-1);
                                        notifyItemRemoved(orgin_size-1);
                                        if(ListParser.getInstance().getMaxPage() > ListParser.getInstance().getCurrentPage()) {
                                            articleData.add(null);
                                            notifyItemInserted(articleData.size());
                                        }
                                    }
                                });
                            } catch(Exception e) {
                                
                            }
                        }
                    }).start();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.articleData.size();
    }
    
    private void setContent(ArticleViewHolder holder, String url) {
        if(holder.loading.getVisibility() == View.GONE)
            return;
            
        ArticleAsyncTask task = new ArticleAsyncTask(holder);
        task.execute(url);
    }
    
    class ArticleAsyncTask extends AsyncTask<String, String, Integer> {
        private ArticleViewHolder holder;
        private String text;
        
        public ArticleAsyncTask(ArticleViewHolder holder) {
            this.holder = holder;
            this.text = "";
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                Document article = Jsoup.connect(params[0]).timeout(60 * 1000).get();
                Elements texts = (article.getElementById("m_content").select("td p").size() > 0 ? article.getElementById("m_content").select("td p") : article.getElementById("m_content").select("td"));
                StringBuffer result = new StringBuffer();
                for(Element e : texts)
                    result.append(e.text()).append("\n");
                text = result.toString();
            } catch(IOException e) {
                return 1;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result == 0) {
                holder.content_text.setText(text);
                holder.loading.setVisibility(View.GONE);
            } else if(result == 1) {
                //Nothing to do
            }
            
            super.onPostExecute(result);
        }
    }
}
