package me.blog.colombia2.schoolparser.tab;

import android.os.*;
import android.support.v4.app.*;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.*;
import me.blog.colombia2.schoolparser.*;
import me.blog.colombia2.schoolparser.sql.ArticleSQLManager;
import me.blog.colombia2.schoolparser.sql.SQLArticle;
import me.blog.colombia2.schoolparser.sql.SQLArticleAdapter;

public class SQLBoardFragment extends Fragment {
    protected RecyclerView articles;
    protected LinearLayout maincontent;

    public ArticleSQLManager manager;
    public SwipeRefreshLayout refresh;

    public SQLBoardFragment() {
        manager = new ArticleSQLManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.view_main_frame, container, false);

        maincontent = (LinearLayout) layout.findViewById(R.id.maincontent);
        refresh = (SwipeRefreshLayout) layout.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SQLBoardAsyncTask().execute();
            }
        });

        articles = (RecyclerView) layout.findViewById(R.id.articles);
        LinearLayoutManager llm = new LinearLayoutManager(container.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        articles.setLayoutManager(llm);
        articles.setHasFixedSize(true);

        task();

        return layout;
    }

    public void task() {
        new SQLBoardAsyncTask().execute();
    }

    class SQLBoardAsyncTask extends AsyncTask<String, String, Integer> {
        private ArrayList<SQLArticle> articleList;
        private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });
        }

        @Override
        protected Integer doInBackground(String[] p1) {
            manager.setCurrentPage(1);
            articleList = manager.getArticles();
            if(articleList == null)
                articleList = new ArrayList<>();
            adapter = new SQLArticleAdapter(SQLBoardFragment.this, articleList);
            if(articleList != null && manager.getMaxPage() > 1) {
                articleList.add(null);
                adapter.notifyItemInserted(articleList.size());
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            refresh.setRefreshing(false);
            articles.setAdapter(adapter);
        }
    }
}
