package me.blog.colombia2.schoolparser;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.tab.*;
import me.blog.colombia2.schoolparser.utils.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import android.util.*;

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
            vh = new ArticleViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.view_content_card, p1, false));
        else
            vh = new LoadMoreHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.view_load_more, p1, false));
        
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
                
            /*//holder.reddot.setTransformationMethod(null);
            SpannableString str = new SpannableString("jjh");
            Resources resources = fragment.getContext().getResources();
            Drawable d = resources.getDrawable(R.drawable.reddot);
            d.setBounds(0, 0, holder.reddot.getLineHeight(), holder.reddot.getLineHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            str.setSpan(span, 0, 1, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.reddot.setText(str);
            if(article.isNew())
                holder.reddot.setVisibility(View.VISIBLE);
            else
                holder.reddot.setVisibility(View.GONE);*/
            if(article.isNew()) {
                SpannableString text = new SpannableString("JJH | "+article.getDate()+" | "+article.getWriter()+" | "+article.getVisitors());
                Drawable d = holder.card.getContext().getResources().getDrawable(R.drawable.newicon);
                d.setBounds(0, 0, holder.dateText.getLineHeight(), holder.dateText.getLineHeight());
                text.setSpan(new ImageSpan(d), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.dateText.setText(text);
            } else
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
                    Intent i = new Intent(MainActivity.instance, AttachmentsActivity.class);
                    i.putExtra("attachments", article.getAttachments());
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
            
            if(article.getTitle().contains("RE:")) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.card.getLayoutParams();
                int occurs = (article.getTitle().length() - article.getTitle().replace("RE:", "").length()) / 3;
                params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 17.0f, holder.card.getContext().getResources().getDisplayMetrics())*occurs;
                holder.card.setLayoutParams(params);
                SpannableString titlespan = new SpannableString(article.getTitle());
                titlespan.setSpan(new ForegroundColorSpan(Color.parseColor("#e53935")), 0, occurs*3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.titleText.setText(titlespan);
            } else {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.card.getLayoutParams();
                params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, holder.card.getContext().getResources().getDisplayMetrics());
                holder.card.setLayoutParams(params);
                holder.titleText.setText(article.getTitle());
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
                public void onClick(final View v) {
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
                                loading = false;
                                articleData.remove(orgin_size-1);
                                notifyItemRemoved(orgin_size-1);
                                if(fragment.parser.getMaxPage() > fragment.parser.getCurrentPage()) {
                                    articleData.add(null);
                                    notifyItemInserted(articleData.size());
                                }
                                ErrorDisplayer.showInternetError(v);
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
