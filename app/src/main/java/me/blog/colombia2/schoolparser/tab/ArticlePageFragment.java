package me.blog.colombia2.schoolparser.tab;

import android.os.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.view.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.utils.*;

public class ArticlePageFragment extends Fragment {
    protected RecyclerView articles;
    protected String menuId;
    
    public ListParser parser;
    public SwipeRefreshLayout refresh;
    
    public ArticlePageFragment() {
        this.parser = new ListParser().setSchoolUrl(SharedConstants.SCHOOL_URL);
        this.menuId = "";
    }
    
    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putString("menuId", menuId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.main, container, false);
        
        if(savedInstanceState != null)
            menuId = savedInstanceState.getString("menuId");
        
        refresh = (SwipeRefreshLayout) layout.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ParserAsyncTask task = new ParserAsyncTask();
                task.execute(menuId);
            }
        });
        
        articles = (RecyclerView) layout.findViewById(R.id.articles);
        LinearLayoutManager llm = new LinearLayoutManager(container.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        articles.setLayoutManager(llm);
        articles.setHasFixedSize(true);
        
        ParserAsyncTask task = new ParserAsyncTask();
        task.execute(menuId);
        
        return layout;
    }
    
    class ParserAsyncTask extends AsyncTask<String, String, Integer> {
        private ArrayList<ArticleData> articleList;
        private ArticleAdapter adapter;

        @Override
        protected void onPreExecute() {
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });

            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                parser.setMenuId(params[0]).setCurrentPage(1).connect();
                articleList = parser.getArticleList();
                if(articleList.size() == 0) {
                    return 0;
                }
                adapter = new ArticleAdapter(ArticlePageFragment.this, articleList);
                if(parser.getMaxPage() > 1) {
                    articleList.add(null);
                    adapter.notifyItemInserted(articleList.size());
                }
            } catch(IOException e) {
                return 1;
            } catch(Exception e) {
                return 2;
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result == 0) {
                refresh.setRefreshing(false);
                articles.setAdapter(adapter);
                if(articleList.size() == 0) {
                    
                }
            } else if(result == 1) {
                refresh.setRefreshing(false);
                articles.setAdapter(null);
                ErrorDisplayer.showInternetError(MainActivity.instance.getWindow().getDecorView());
            } else if(result == 2) {
                refresh.setRefreshing(false);
                articles.setAdapter(null);
            }

            super.onPostExecute(result);
        }
    }
}
