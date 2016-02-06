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
    
    protected LinearLayout mainContent;
    protected RecyclerView articles;
    protected SwipeRefreshLayout refresh;
    protected Button page;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getSupportActionBar().setTitle(R.string.loading);
        
        mainContent = (LinearLayout) findViewById(R.id.maincontent);
        
        page = (Button) findViewById(R.id.page);
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
            public void onFinish(final String category, final int total, final ArrayList<String[]> list, final ArrayList<ArrayList<String[]>> files) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        articles.setVisibility(View.VISIBLE);
                        refresh.setRefreshing(false);
                        ArticleAdapter adapter = new ArticleAdapter(MainActivity.this, list, files);
                        articles.setAdapter(adapter);
                        
                        
                        
                        getSupportActionBar().setTitle(category);
                        page.setText(SharedConstants.PARSER.currentPage+"/"+total);
                        page.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LinearLayout layout = new LinearLayout(MainActivity.this);
                                layout.setGravity(Gravity.CENTER);
                                
                                final NumberPicker picker = new NumberPicker(MainActivity.this);
                                picker.setMinValue(1);
                                picker.setMaxValue(total);
                                picker.setValue(SharedConstants.PARSER.currentPage);
                                layout.addView(picker);
                                
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setView(layout);
                                builder.setTitle("페이지 선택");
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        SharedConstants.PARSER.setPage(picker.getValue());
                                        page.setText(SharedConstants.PARSER.currentPage+"/"+total);
                                        refresh();
                                    }
                                });
                                builder.setNegativeButton("취소", null);
                                builder.create().show();
                            }
                        });
                    }
                });
            }
            
            @Override
            public void onInternetError(final Exception e) {
                android.util.Log.i("affoparser", e+"");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        articles.setVisibility(View.INVISIBLE);
                        final Snackbar snackbar = Snackbar.make(mainContent, R.string.check_internet, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                refresh();
                            }
                        });
                        snackbar.show();
                        refresh.setRefreshing(false);
                        
                        getSupportActionBar().setTitle(R.string.internet_error);
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
            case R.id.categories_1:
                SharedConstants.PARSER.setPage(1);
                SharedConstants.CURRENT_CATEGORY = 0;
                SharedConstants.PARSER.setUrl(SharedConstants.URLS[SharedConstants.CURRENT_CATEGORY]);
                refresh();
                return true;
                
            case R.id.categories_2:
                SharedConstants.PARSER.setPage(1);
                SharedConstants.CURRENT_CATEGORY = 1;
                SharedConstants.PARSER.setUrl(SharedConstants.URLS[SharedConstants.CURRENT_CATEGORY]);
                refresh();
                return true;
                
            case R.id.categories_3:
                SharedConstants.PARSER.setPage(1);
                SharedConstants.CURRENT_CATEGORY = 2;
                SharedConstants.PARSER.setUrl(SharedConstants.URLS[SharedConstants.CURRENT_CATEGORY]);
                refresh();
                return true;
                
            case R.id.categories_4:
                SharedConstants.PARSER.setPage(1);
                SharedConstants.CURRENT_CATEGORY = 3;
                SharedConstants.PARSER.setUrl(SharedConstants.URLS[SharedConstants.CURRENT_CATEGORY]);
                refresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    protected void refresh() {
        refresh.setRefreshing(true);
        getSupportActionBar().setTitle(R.string.loading);
        SharedConstants.PARSER.start();
    }
    
    public void changeActivity(ArrayList<String[]> files) {
        Intent i = new Intent(MainActivity.this, AttachmentsActivity.class);
        SharedConstants.ATTACHMENTS = files;
        startActivity(i);
    }
}
