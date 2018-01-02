package me.blog.colombia2.schoolparser.sql;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import java.io.*;
import java.util.*;

import me.blog.colombia2.schoolparser.ArticleActivity;
import me.blog.colombia2.schoolparser.ArticleViewHolder;
import me.blog.colombia2.schoolparser.LoadMoreHolder;
import me.blog.colombia2.schoolparser.MainActivity;
import me.blog.colombia2.schoolparser.R;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.sql.ArticleSQLManager;
import me.blog.colombia2.schoolparser.sql.SQLArticle;
import me.blog.colombia2.schoolparser.tab.*;
import me.blog.colombia2.schoolparser.utils.*;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.*;
import org.jsoup.nodes.*;

public class SQLArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected SQLBoardFragment fragment;
    protected ArrayList<SQLArticle> articleData;
    protected boolean loading;

    public SQLArticleAdapter(SQLBoardFragment fragment, ArrayList<SQLArticle> articleData) {
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
        final SQLArticle article = articleData.get(position);

        if(a instanceof ArticleViewHolder) {
            final ArticleViewHolder holder = (ArticleViewHolder) a;
            holder.closeHolder(false);
            holder.loading.setVisibility(View.GONE);
            holder.content_text.setText(article.getContent());
            holder.opened = false;

            if(article.getReplyCount() > 0)
                holder.titleText.setText(article.getTitle()+" ["+article.getReplyCount()+"]");
            else
                holder.titleText.setText(article.getTitle());


            DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            boolean isNew = Days.daysBetween(format.parseDateTime(article.getWdate()), DateTime.now()).getDays() <= 4;
            if(isNew && fragment.getContext().getSharedPreferences("appData", Context.MODE_PRIVATE).getBoolean("newVisible", true)) {
                SpannableString text = new SpannableString("JJH | "+article.getWdate()+" | "+article.getName()+" | "+article.getView()+" | "+article.getIp().substring(0, article.getIp().lastIndexOf("."))+".xxx");
                Drawable d = holder.card.getContext().getResources().getDrawable(R.drawable.newicon);
                d.setBounds(0, 0, holder.dateText.getLineHeight(), holder.dateText.getLineHeight());
                text.setSpan(new ImageSpan(d), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.dateText.setText(text);
            } else
                holder.dateText.setText(article.getWdate()+" | "+article.getName()+" | "+article.getView()+" | "+article.getIp().substring(0, article.getIp().lastIndexOf("."))+".xxx");

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fragment.refresh.isRefreshing())
                        return;

                    if(holder.isOpened())
                        holder.closeHolder(true);
                    else {
                        holder.openHolder(true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ArticleSQLManager.updateArticle(article.getId(), article.getTitle(), article.getContent(), article.getView()+1);
                            }
                        }).start();
                    }
                }
            });

            holder.content_readall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.instance, ArticleActivity.class);
                    i.putExtra("mode", 1);
                    i.putExtra("article", new String[]{article.getTitle(), article.getContent().replace("\n", "<br/>"), article.getName(), article.getWdate()});
                    MainActivity.instance.startActivity(i);
                }
            });


            holder.content_attachments.setText("게시글 삭제");
            holder.content_attachments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("비밀번호 입력");
                    builder.setMessage("게시글 비밀번호를 입력해주세요.");
                    final AppCompatEditText editText = new AppCompatEditText(v.getContext());
                    editText.setHint("비밀번호");
                    builder.setView(editText);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(article.getPass().equals(ArticleUtils.sha1(editText.getText().toString()))) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArticleSQLManager.deleteArticle(article.getId());
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                fragment.task();
                                            }
                                        });
                                    }
                                }).start();

                            } else {
                                ErrorDisplayer.showError(v, "비밀번호가 일치하지 않습니다");
                            }
                        }
                    });
                    builder.setNegativeButton("취소", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });


            holder.content_replies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.instance, SQLReplyActivity.class);
                    i.putExtra("article_id", article.getId());
                    MainActivity.instance.startActivity(i);
                }
            });
/*
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
            }*/
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
                                final ArticleSQLManager manager = fragment.manager;
                                manager.setCurrentPage(manager.getCurrentPage() + 1);
                                final ArrayList<SQLArticle> arr = manager.getArticles();
                                articleData.addAll(arr);

                                final int maxpage = manager.getMaxPage();
                                final int currentpage = manager.getCurrentPage();

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyItemRangeInserted(orgin_size, arr.size());
                                        loading = false;
                                        articleData.remove(orgin_size-1);
                                        notifyItemRemoved(orgin_size-1);
                                        if(maxpage > currentpage) {
                                            articleData.add(null);
                                            notifyItemInserted(articleData.size());
                                        }
                                    }
                                });
                            } catch(Exception e) {
                                e.printStackTrace();
                                loading = false;
                                articleData.remove(orgin_size-1);
                                notifyItemRemoved(orgin_size-1);
                                if(fragment.manager.getMaxPage() > fragment.manager.getCurrentPage()) {
                                    articleData.add(null);
                                    notifyItemInserted(articleData.size());
                                }
                                ErrorDisplayer.showInternetError(fragment.getView());
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
}
