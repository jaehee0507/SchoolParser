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
import me.blog.colombia2.schoolparser.utils.*;
import java.io.*;
import android.support.design.widget.*;

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
    
    final private static String SCHOOL_URL = "http://cw.hs.kr";
    
    protected LinearLayout mainContent;
    protected RecyclerView articles;
    protected SwipeRefreshLayout refresh;
    
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
                try {
                    ListParser parser = ListParser.getInstance()
                                         .setSchoolUrl(SCHOOL_URL)
                                         .setMenuId(ListParser.getInstance().getMenuId().equals("") ? SharedConstants.MENUS[0] : ListParser.getInstance().getMenuId())
                                         .setFilteringNotice(true)
                                         .connect();
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
                } catch(IOException e) {
                    showInternetError();
                }
            }
        }).start();
    }
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        final SubMenu subMenu = menu.getItem(0).getSubMenu();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < SharedConstants.MENUS.length; i++) {
                    final int index = i;
                    final StringBuffer buffer = new StringBuffer();
                    try {
                        buffer.append(MenuTitleParser.getTitle(SCHOOL_URL, SharedConstants.MENUS[i]));
                    } catch(IOException e) {
                        buffer.append(getResources().getString(R.string.internet_error));
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            subMenu.add(0, index, Menu.NONE, buffer.toString())
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        refresh.setRefreshing(true);
                                        getSupportActionBar().setTitle(R.string.loading);
                                        ListParser.getInstance().setMenuId(SharedConstants.MENUS[item.getItemId()]);
                                        refresh();
                                        return true;
                                    }
                                });
                        }
                    });
                }
            }
        }).start();
        
        return true;
    }
    
    protected void showInternetError() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(false);
                articles.setAdapter(null);
                final Snackbar snackbar = Snackbar.make(mainContent, R.string.check_internet, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.retry,
                                   new View.OnClickListener() {
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
        });
    }
    
    protected void refresh() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    ArrayList<ArticleData> articleList = ListParser.getInstance().connect().getArticleList();
                    final ArticleAdapter adapter = new ArticleAdapter(MainActivity.this, articleList);
                    final String menuName = ListParser.getInstance().getTitle();
                    handler.post(new Runnable() {
                            @Override
                            public void run() {
                                articles.setAdapter(adapter);
                                refresh.setRefreshing(false);
                                getSupportActionBar().setTitle(menuName);
                            }
                        });
                } catch(IOException e) {
                    showInternetError();
                }
            }
        }).start();
    }
    
    public void changeActivity(ArrayList<FileData> files) {
        Intent i = new Intent(MainActivity.this, AttachmentsActivity.class);
        SharedConstants.ATTACHMENTS = files;
        startActivity(i);
    }
}
