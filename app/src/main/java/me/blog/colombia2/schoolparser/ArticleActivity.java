package me.blog.colombia2.schoolparser;

import android.support.v7.app.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import org.jsoup.nodes.*;
import org.jsoup.*;
import java.io.*;

public class ArticleActivity extends AppCompatActivity {
    WebView webview;
    String url;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        webview = (WebView) findViewById(R.id.web);
        webview.getSettings().setJavaScriptEnabled(true);
        
        if(savedInstanceState == null)
            url = getIntent().getStringExtra("url");
        else
            url = savedInstanceState.getString("url");
            
        new ArticleAsyncTask().execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putString("url", url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    class ArticleAsyncTask extends AsyncTask<String, String, Integer> {
        String html;
        
        @Override
        protected Integer doInBackground(String[] p1) {
            try {
                Document doc = Jsoup.connect(url)
                    .timeout(10 * 1000)
                    .get();
                html = doc.getElementById("m_content").select("td").first().toString();
            } catch(IOException e) {
                
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            webview.loadData(html, "text/html; charset=UTF-8", null);
            super.onPostExecute(result);
        }
    }
}
