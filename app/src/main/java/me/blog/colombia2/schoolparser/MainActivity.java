package me.blog.colombia2.schoolparser;

import android.content.*;
import android.os.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;

public class MainActivity extends AppCompatActivity  {
    private class MyScrollListener extends RecyclerView.OnScrollListener {
        private LinearLayoutManager llm;
        
        public MyScrollListener(LinearLayoutManager llm) {
            super();
            this.llm = llm;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            refresh.setEnabled(llm.findFirstCompletelyVisibleItemPosition() == 0);
        }
    }
    
    protected LinearLayout mainContent;
    protected RecyclerView articles;
    protected SwipeRefreshLayout refresh;
    
    protected ListParser parser;
    protected Handler handler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getSupportActionBar().setTitle(R.string.loading);
        
        mainContent = (LinearLayout) findViewById(R.id.maincontent);
        
        articles = (RecyclerView) findViewById(R.id.articles);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        articles.setLayoutManager(llm);
        articles.setHasFixedSize(true);
        articles.setOnScrollListener(new MyScrollListener(llm));
        
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                getSupportActionBar().setTitle(R.string.loading);
            }
        });
        refresh.post(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(true);
                getSupportActionBar().setTitle(R.string.loading);
            }
        });
        handler = new Handler();
        
        new Thread(new Runnable() {
            public void run() {
                parser = new ListParser("http://cw.hs.kr", SharedConstants.MENUS[0]);
                parser.setFilteringNotice(true);
                ArrayList<ArticleData> articleList = parser.getArticleList();
                final ArticleAdapter adapter = new ArticleAdapter(MainActivity.this, articleList);
                final String menuName = parser.getTitle();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        articles.setAdapter(adapter);
                        refresh.setRefreshing(false);
                        getSupportActionBar().setTitle(menuName);
                    }
                });
            }
        }).start();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() != R.id.categories) {
            refresh.setRefreshing(true);
            getSupportActionBar().setTitle(R.string.loading);
        } else {
            return super.onOptionsItemSelected(item);
        }
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch(item.getItemId()) {
                    case R.id.categories_1:
                        parser.setMenuId(SharedConstants.MENUS[0]);
                        refresh();
                        break;
                 
                    case R.id.categories_2:
                        parser.setMenuId(SharedConstants.MENUS[1]);
                        refresh();
                        break;
                
                    case R.id.categories_3:
                        parser.setMenuId(SharedConstants.MENUS[2]);
                        refresh();
                        break;
                
                    case R.id.categories_4:
                        parser.setMenuId(SharedConstants.MENUS[3]);
                        refresh();
                        break;
                }
            }
        }).start();

        return super.onOptionsItemSelected(item);
    }
    
    protected void refresh() {
        new Thread(new Runnable() {
            public void run() {
                ArrayList<ArticleData> articleList = parser.getArticleList();
                final ArticleAdapter adapter = new ArticleAdapter(MainActivity.this, articleList);
                final String menuName = parser.getTitle();
                handler.post(new Runnable() {
                     @Override
                     public void run() {
                         articles.setAdapter(adapter);
                         refresh.setRefreshing(false);
                         getSupportActionBar().setTitle(menuName);
                     }
                });
            }
        }).start();
    }
    
    public void changeActivity(ArrayList<FileData> files) {
        Intent i = new Intent(MainActivity.this, AttachmentsActivity.class);
        SharedConstants.ATTACHMENTS = files;
        startActivity(i);
    }
}
