package me.blog.colombia2.schoolparser;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.tab.*;
import me.blog.colombia2.schoolparser.utils.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected ArticlePageFragment fragment;
    protected ArrayList<ArticleData> articleData;
    protected boolean loading;
    
    public ArticleAdapter(ArticlePageFragment fragment, ArrayList<ArticleData> articleData) {
        this.fragment = fragment;
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
            holder.dateText.setText(article.getDate()+" | "+article.getWriter()+" | "+article.getVisitors());
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fragment.refresh.isRefreshing())
                        return;
                    
                    if(holder.isOpened())
                        holder.closeHolder(true);
                    else {
                        holder.openHolder(true);
                        setContent(holder, article.getHyperLink());
                    }
                }
            });
            
            if(article.isNotice()) {
                holder.card.setCardBackgroundColor(Color.parseColor("#FF8A65"));
            } else {
                holder.card.setCardBackgroundColor(holder.card.getContext().getResources().getColor(R.color.cardview_light_background));
            }
        
            holder.content_readall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.instance, ArticleActivity.class);
                    i.putExtra("url", article.getHyperLink());
                    MainActivity.instance.startActivity(i);
                }
            });
        
            holder.content_attachments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedConstants.ATTACHMENTS = article.getAttachments();
                    Intent i = new Intent(MainActivity.instance, AttachmentsActivity.class);
                    MainActivity.instance.startActivity(i);
                }
            });
            
            if(article.hasReply()) {
                holder.content_replies.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.instance, ReplyActivity.class);
                        i.putExtra("hyperlink", article.getHyperLink());
                        MainActivity.instance.startActivity(i);
                    }
                });
            } else {
                holder.content_replies.setOnClickListener(null);
            }
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
                                final ListParser parser = fragment.parser;
                                parser.setCurrentPage(parser.getCurrentPage() + 1)
                                      .connect().get();
                                final ArrayList<ArticleData> arr = parser.getArticleList();
                                articleData.addAll(arr);
                                
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyItemRangeInserted(orgin_size, arr.size());
                                        loading = false;
                                        articleData.remove(orgin_size-1);
                                        notifyItemRemoved(orgin_size-1);
                                        if(parser.getMaxPage() > parser.getCurrentPage()) {
                                            articleData.add(null);
                                            notifyItemInserted(articleData.size());
                                        }
                                    }
                                });
                            } catch(IOException e) {
                                e.printStackTrace();
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
        private Spanned text;
        
        public ArticleAsyncTask(ArticleViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                Document article = Jsoup.connect(params[0]).timeout(60 * 1000).get();
                text = Html.fromHtml(article.getElementById("m_content").select("td").first().toString().replace("/files", "http://cw.hs.kr/files").replace("alt=", "width=100% alt="));
            } catch(IOException e) {
                return 1;
            } catch(Exception e) {
                text = new SpannedString("권한이 없습니다.");
                return 0;
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
