package me.blog.colombia2.schoolparser.tab;

import android.graphics.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.utils.*;
import com.github.takahirom.webview_in_coodinator_layout.NestedWebView;

public class ArticlePageFragment extends Fragment {
    protected RecyclerView articles;
    protected LinearLayout maincontent;
    protected String menuId;

    public ListParser parser;
    public SwipeRefreshLayout refresh;
    public NestedWebView webview;

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

        webview = (NestedWebView) layout.findViewById(R.id.webview);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.getSettings().setDefaultTextEncodingName("utf-8");
		webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        maincontent = (LinearLayout) layout.findViewById(R.id.maincontent);
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
        private String contentUrl;

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
                if(parser.isArticleInItForm()) {
                    contentUrl = parser.getNonArticleContent();
                    return 3;
                }
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
            } else if(result == 3) {
                webview.loadUrl(contentUrl);
                maincontent.setVisibility(View.GONE);
                webview.setVisibility(View.VISIBLE);
            }

            super.onPostExecute(result);
        }
    }
}
