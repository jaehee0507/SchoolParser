package me.blog.colombia2.schoolparser;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import java.util.*;

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
    
    protected RelativeLayout mainContent;
    protected RecyclerView articles;
    protected SwipeRefreshLayout refresh;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getSupportActionBar().setTitle(R.string.loading);
        
        mainContent = (RelativeLayout) findViewById(R.id.maincontent);
        
        articles = (RecyclerView) findViewById(R.id.articles);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        articles.setLayoutManager(llm);
        articles.setOnScrollListener(new MyScrollListener(llm));
        
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedConstants.PARSER.start();
            }
        });
        refresh.post(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(true);
            }
        });
        
        SharedConstants.PARSER = new Parser();
        SharedConstants.PARSER.setUrl(SharedConstants.URLS[SharedConstants.CURRENT_CATEGORY]);
        SharedConstants.PARSER.setOnParseFinishListener(new Parser.OnParseFinishListener() {
            @Override
            public void onFinish(final ArrayList<String[]> list, final ArrayList<ArrayList<String[]>> files) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        articles.setVisibility(View.VISIBLE);
                        refresh.setRefreshing(false);
                        ArticleAdapter adapter = new ArticleAdapter(MainActivity.this, list, files);
                        articles.setAdapter(adapter);
                        
                        getSupportActionBar().setTitle(SharedConstants.CATEGORY_NAMES[SharedConstants.CURRENT_CATEGORY]);
                    }
                });
            }
            
            @Override
            public void onInternetError(final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        articles.setVisibility(View.INVISIBLE);
                        final Snackbar snackbar = Snackbar.make(mainContent, R.string.check_internet, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                SharedConstants.PARSER.start();
                            }
                        });
                        snackbar.show();
                        refresh.setRefreshing(false);
                        
                        getSupportActionBar().setTitle(R.string.app_name);
                    }
                });
            }
        });
        SharedConstants.PARSER.start();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:
                return true;
                
            case R.id.categories_1:
                refresh.setRefreshing(true);
                getSupportActionBar().setTitle(R.string.loading);
                SharedConstants.CURRENT_CATEGORY = 0;
                SharedConstants.PARSER.setUrl(SharedConstants.URLS[SharedConstants.CURRENT_CATEGORY]);
                SharedConstants.PARSER.start();
                return true;
                
            case R.id.categories_2:
                refresh.setRefreshing(true);
                getSupportActionBar().setTitle(R.string.loading);
                SharedConstants.CURRENT_CATEGORY = 1;
                SharedConstants.PARSER.setUrl(SharedConstants.URLS[SharedConstants.CURRENT_CATEGORY]);
                SharedConstants.PARSER.start();
                return true;
                
            case R.id.categories_3:
                refresh.setRefreshing(true);
                getSupportActionBar().setTitle(R.string.loading);
                SharedConstants.CURRENT_CATEGORY = 2;
                SharedConstants.PARSER.setUrl(SharedConstants.URLS[SharedConstants.CURRENT_CATEGORY]);
                SharedConstants.PARSER.start();
                return true;
                
            case R.id.categories_4:
                refresh.setRefreshing(true);
                getSupportActionBar().setTitle(R.string.loading);
                SharedConstants.CURRENT_CATEGORY = 3;
                SharedConstants.PARSER.setUrl(SharedConstants.URLS[SharedConstants.CURRENT_CATEGORY]);
                SharedConstants.PARSER.start();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    public void changeActivity(ArrayList<String[]> files) {
        Intent i = new Intent(MainActivity.this, AttachmentsActivity.class);
        SharedConstants.ATTACHMENTS = files;
        startActivity(i);
    }
}
