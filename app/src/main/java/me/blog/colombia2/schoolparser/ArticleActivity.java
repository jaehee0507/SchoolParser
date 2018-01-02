package me.blog.colombia2.schoolparser;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import me.blog.colombia2.schoolparser.utils.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

public class ArticleActivity extends AppCompatActivity {
    int mode;
    WebView webview;
    View progress;
    String url;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_webview);

        mode = getIntent().getIntExtra("mode", 0);
        
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        progress = findViewById(R.id.toolbar_progress);
        webview = (WebView) findViewById(R.id.web);
        webview.getSettings().setJavaScriptEnabled(true);
        if(mode == 0) {
            webview.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView wv, int prgrs) {
                    float prog = ((float) prgrs) / 100.0f;
                    progress.setLayoutParams(new RelativeLayout.LayoutParams((int) (prog * webview.getWidth()), (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            5.0f,
                            getResources().getDisplayMetrics())));

                    if (prgrs == 100) {
                        progress.setLayoutParams(new RelativeLayout.LayoutParams(0, (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                5.0f,
                                getResources().getDisplayMetrics())));
                    }
                }
            });
            registerForContextMenu(webview);

            if (savedInstanceState == null)
                url = getIntent().getStringExtra("url");
            else
                url = savedInstanceState.getString("url");

            new ArticleAsyncTask().execute();
        } else if(mode == 1) {
            String[] data = getIntent().getStringArrayExtra("article");
            String html  =  "<html>"+
                            "<head>\n" +
                            "<meta content=\"text/html; charset=utf-8\">\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<p style=\"font-size:1.5em; font-weight: bold;\">"+data[0]+"</p>\n" +
                            "<p style=\"font-size:0.7em;\">작성자 : "+data[2]+"<br/>게시일 : "+data[3]+"</p>\n"+
                            "<hr noshade/>\n" +
                            "<p>"+data[1]+"</p>\n"+
                            "</body>\n" +
                            "</html>";
            webview.loadDataWithBaseURL(null, html, "text/html", "UTF-8", "about:blank");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        
        final WebView.HitTestResult result = webview.getHitTestResult();
        if(result.getType() == WebView.HitTestResult.IMAGE_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            menu.add("이미지 저장").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    try {
                        FileDownloader downloader = new FileDownloader(result.getExtra(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+result.getExtra().split("/")[result.getExtra().split("/").length-1]);
                        downloader.setFileDownloadListener(new FileDownloader.FileDownloadListener() {
                            public void onDownloadStart(int fileSize) {}
                            public void onDownloading(int currentBytes) {}
                            public void onDownloadError(Exception err) {}
                            public void onDownloadComplete(final File result) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(result.getPath().length() > 37)
                                            Snackbar.make(getWindow().getDecorView(), result.getPath().substring(0, 17)+"..."+result.getPath().substring(result.getPath().length()-17, result.getPath().length())+" 에 다운로드 완료", Snackbar.LENGTH_SHORT)
                                            .setAction("열기", new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    Intent toLaunch = new Intent();
                                                    toLaunch.setAction(Intent.ACTION_VIEW);
                                                    toLaunch.setDataAndType(Uri.fromFile(result), getContentResolver().getType(Uri.fromFile(result)));
                                                    startActivity(toLaunch);
                                                }
                                            }).show();
                                        else
                                            Snackbar.make(getWindow().getDecorView(), result.getPath()+" 에 다운로드 완료", Snackbar.LENGTH_SHORT)
                                            .setAction("열기", new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    Intent toLaunch = new Intent();
                                                    toLaunch.setAction(Intent.ACTION_VIEW);
                                                    toLaunch.setDataAndType(Uri.fromFile(result), getContentResolver().getType(Uri.fromFile(result)));
                                                    startActivity(toLaunch);
                                                }
                                            }).show();
                                    }
                                });
                            }
                        });
                        downloader.start();
                    } catch(MalformedURLException e) {
                        
                    }
                    return true;
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mode == 0)
            outState.putString("url", url);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mode == 0)
            getMenuInflater().inflate(R.menu.article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        } else if(item.getItemId() == R.id.export) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
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
                String date = doc.getElementById("m_mainView").select("tr").get(1).select("td").get(3).text();
                html = doc.getElementById("m_content").select("td").first().toString().replace("/files", "http://cw.hs.kr/files").replace("alt=", "width=100% alt=")+"<br><br>등록일 : "+date;
            } catch(IOException e) {
                
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            webview.loadDataWithBaseURL(null, html, "text/html", "UTF-8", "about:blank");
            super.onPostExecute(result);
        }
    }
}
