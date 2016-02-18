package me.blog.colombia2.schoolparser.tab;

import android.os.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.utils.*;
import android.util.*;

public class ArticlePageFragment extends Fragment {
    protected RecyclerView articles;
    
    public ListParser parser;
    public SwipeRefreshLayout refresh;
    
    public ArticlePageFragment() {
        this.parser = new ListParser().setSchoolUrl(SharedConstants.SCHOOL_URL);
    }
    
    public void setMenuId(String menuId) {
        this.parser.setMenuId(menuId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.main, container, false);
        
        refresh = (SwipeRefreshLayout) layout.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ParserAsyncTask task = new ParserAsyncTask();
                task.execute();
            }
        });
        
        articles = (RecyclerView) layout.findViewById(R.id.articles);
        LinearLayoutManager llm = new LinearLayoutManager(container.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        articles.setLayoutManager(llm);
        articles.setHasFixedSize(true);
        
        ParserAsyncTask task = new ParserAsyncTask();
        task.execute();
        
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
                parser.setCurrentPage(1).connect();
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
                Log.e("affoparser", e+"");
                return 2;
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result == 0) {
                refresh.setRefreshing(false);
                articles.setAdapter(adapter);
            } else if(result == 1) {
                
            } else if(result == 2) {
                refresh.setRefreshing(false);
                articles.setAdapter(null);
            }

            super.onPostExecute(result);
        }
    }
}
