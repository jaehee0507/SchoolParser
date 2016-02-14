package me.blog.colombia2.schoolparser;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.utils.*;
import android.util.*;

public class MainActivity extends AppCompatActivity  {
    private class MyScrollListener extends RecyclerView.OnScrollListener {
        public MyScrollListener() {
            super();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            refresh.setEnabled(!recyclerView.canScrollVertically(-1));
        }
    }
    
    protected LinearLayout mainContent;
    protected RecyclerView articles;
    protected SwipeRefreshLayout refresh;
    
    protected ArrayList<ArticleData> articleList;
    protected ArticleAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedConstants.MENUS.size() == 0) {
            Intent i = new Intent(MainActivity.this, SchoolSettingActivity.class);
            startActivity(i);
            
            finish();
        }
        setContentView(R.layout.main);
        
        getSupportActionBar().setTitle(R.string.loading);
        
        mainContent = (LinearLayout) findViewById(R.id.maincontent);
        
        articles = (RecyclerView) findViewById(R.id.articles);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        articles.setLayoutManager(llm);
        articles.setHasFixedSize(true);
        articles.setOnScrollListener(new MyScrollListener());
        
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        
        ParserAsyncTask asyncTask = new ParserAsyncTask();
        asyncTask.execute(SharedConstants.SCHOOL_URL, SharedConstants.MENUS.get(0));
    }
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getSharedPreferences("schoolData", MODE_PRIVATE).edit().clear().commit();
                Intent i = new Intent(MainActivity.this, SchoolSettingActivity.class);
                startActivity(i);
                finish();
                return true;
            }
        });
        final SubMenu sub = menu.getItem(0).getSubMenu();
        for(int i = 0; i < SharedConstants.MENUS.size(); i++)
            sub.add(0, i, Menu.NONE, SharedConstants.MENU_NAMES.get(i))
               .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem item) {
                       ListParser.getInstance().setMenuId(SharedConstants.MENUS.get(item.getItemId()));
                       refresh();
                       return true;
                   }
               });
        
        return true;
    }
    
    protected void showInternetError() {
        refresh.setRefreshing(false);
        articles.setAdapter(null);
        final Snackbar snackbar = Snackbar.make(mainContent, R.string.check_internet, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                refresh.setRefreshing(true);
                getSupportActionBar().setTitle(R.string.loading);
                refresh();
            }
        });
        snackbar.show();
        getSupportActionBar().setTitle(R.string.internet_error);
    }
    
    protected void refresh() {
        ParserAsyncTask asyncTask = new ParserAsyncTask();
        asyncTask.execute(SharedConstants.SCHOOL_URL, ListParser.getInstance().getMenuId());
    }
    
    public void changeActivity(ArrayList<FileData> files) {
        Intent i = new Intent(MainActivity.this, AttachmentsActivity.class);
        SharedConstants.ATTACHMENTS = files;
        startActivity(i);
    }
    
    class ParserAsyncTask extends AsyncTask<String, String, Integer> {
        private String menuName;

        @Override
        protected void onPreExecute() {
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });
            getSupportActionBar().setTitle(R.string.loading);
            
            super.onPreExecute();
        }
        
        @Override
        protected Integer doInBackground(String... params) {
            try {
                ListParser parser = ListParser.getInstance()
                                    .setCurrentPage(1)
                                    .setSchoolUrl(params[0])
                                    .setMenuId(params[1])
                                    .setFilteringNotice(true)
                                    .connect();
                menuName = parser.getTitle();
                articleList = parser.getArticleList();
                adapter = new ArticleAdapter(MainActivity.this, articleList);
                if(ListParser.getInstance().getMaxPage() > 1) {
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
                getSupportActionBar().setTitle(menuName);
            } else if(result == 1) {
                showInternetError();
            } else if(result == 2) {
                refresh.setRefreshing(false);
                articles.setAdapter(null);
                getSupportActionBar().setTitle(menuName);
            }
            
            super.onPostExecute(result);
        }
    }
}
