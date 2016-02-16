package me.blog.colombia2.schoolparser;

import android.content.*;
import android.net.*;
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
        
        SharedPreferences appData = getSharedPreferences("appData", MODE_PRIVATE);
        boolean cautionIgnore = appData.getBoolean("cautionIgnore", false);
        if(isMobileNetwork() && !cautionIgnore) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("주의");
            builder.setMessage("3G/LTE와 같은 모바일 네트워크 환경에서 이용 시 과다 데이터 부과요금이 발생할 수 있습니다.");
            builder.setPositiveButton("확인", null);
            builder.setNegativeButton("다시 보지 않기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int i) {
                    SharedPreferences.Editor edit = getSharedPreferences("appData", MODE_PRIVATE).edit();
                    edit.putBoolean("cautionIgnore", true);
                    edit.commit();
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    startParser();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            startParser();
        }
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
    
    protected void startParser() {
        SharedPreferences pref = getSharedPreferences("schoolData", MODE_PRIVATE);
        if(!pref.getString("menulist", "null").equals("null")) {
            SharedConstants.MENUS = new ArrayList<String>(Arrays.asList(pref.getString("menulist", "").split(";")));
            SharedConstants.MENU_NAMES = new ArrayList<String>(Arrays.asList(pref.getString("menunames", "").split(";")));
        }
        ParserAsyncTask asyncTask = new ParserAsyncTask();
        asyncTask.execute(SharedConstants.SCHOOL_URL, SharedConstants.MENUS.get(0));
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
    
    public boolean isMobileNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if(isConnected) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
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
                                    //.setFilteringNotice(true)
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
