package me.blog.colombia2.schoolparser.tab;

import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.github.takahirom.webview_in_coodinator_layout.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.utils.*;

import me.blog.colombia2.schoolparser.R;

public class ArticlePageFragment extends Fragment {
    protected RecyclerView articles;
    protected LinearLayout maincontent;
    protected String menuId;
    protected TabLayout.Tab tab;

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
    
    public void setTab(TabLayout.Tab tab) {
        this.tab = tab;
    }
    
    public TabLayout.Tab getTab() {
        return tab;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("menuId", menuId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.view_main_frame, container, false);

        if(savedInstanceState != null) {
            menuId = savedInstanceState.getString("menuId");
        }

        webview = (NestedWebView) layout.findViewById(R.id.webview);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDefaultTextEncodingName("utf-8");
		webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setPluginState(WebSettings.PluginState.ON);
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

        task();

        return layout;
    }
    
    public void task() {
        ParserAsyncTask task = new ParserAsyncTask();
        task.execute(menuId);
    }

    class ParserAsyncTask extends AsyncTask<String, String, Integer> {
        private ArrayList<ArticleData> articleList;
        private ArrayList<ScheduleData> scheduleList;
        private ArrayList<PhotoData> photoList;
        private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
        private String content;

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
                parser.setMenuId(params[0])
                      .setCurrentPage(1).connect().get();
                if(parser.getMenuId().equals("M001002013")) {
                    photoList = parser.getPhotoList();
                    adapter = new PhotoAdapter(ArticlePageFragment.this, photoList);
                    if(parser.getMaxPage() > 1) {
                        photoList.add(null);
                        adapter.notifyItemChanged(photoList.size());
                    }

                    return 4;
                }
                if(parser.isNonArticleForm()) {
                    content = parser.getNonArticleContent();
                    return 3;
                }
				if(parser.isMonthListForm()) {
                    int year = 0, month = 0;
                    if(((String) tab.getTag()).equals("")) {
                        year = Calendar.getInstance().get(Calendar.YEAR);
                        month = Calendar.getInstance().getTime().getMonth();
                        tab.setTag(year+";"+month);
                    } else {
                        year = Integer.parseInt(((String) tab.getTag()).split(";")[0]);
                        month = Integer.parseInt(((String) tab.getTag()).split(";")[1]);
                    }
                    
					scheduleList = parser.getMonthListContent(year, month);
                    adapter = new ScheduleAdapter(ArticlePageFragment.this, scheduleList);
					return 4;
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
            } else if(result == 1) {
                refresh.setRefreshing(false);
                articles.setAdapter(null);
                ErrorDisplayer.showInternetError(MainActivity.instance.getWindow().getDecorView());
            } else if(result == 2) {
                refresh.setRefreshing(false);
                articles.setAdapter(null);
                ErrorDisplayer.showError(maincontent, "알 수 없는 오류가 발생했습니다");
            } else if(result == 3) {
                webview.loadUrl(content);
                maincontent.setVisibility(View.INVISIBLE);
                webview.setVisibility(View.VISIBLE);
            } else if(result == 4) {
                refresh.setRefreshing(false);
                articles.setAdapter(adapter);
            }

            super.onPostExecute(result);
        }
    }
}
