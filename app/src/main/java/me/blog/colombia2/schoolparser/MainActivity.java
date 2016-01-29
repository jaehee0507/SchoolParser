package me.blog.colombia2.schoolparser;

import android.app.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import android.net.*;
import android.view.*;
import android.content.*;
import java.util.*;

public class MainActivity extends Activity  {
    private Parser parser;
    private ListView listView;
    private Button refresh;
    private TextView interneterr;
    private BroadcastReceiver completeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent i) {
            Toast.makeText(ctx, "/sdcard/Download/에 다운로드가 완료되었습니다.", Toast.LENGTH_SHORT).show();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        listView = (ListView) findViewById(R.id.listview);
        interneterr = (TextView) findViewById(R.id.interneterr);
        refresh = (Button) findViewById(R.id.refresh);
        refresh.setClickable(false);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setAdapter(null);
                parser.start();
                refresh.setClickable(false);
                interneterr.setVisibility(View.GONE);
            }
        });
        
        parser = new Parser("http://cw.hs.kr/index.jsp?SCODE=S0000000213&mnu=M001013", 
                new Parser.onParseFinishListener() {
                    @Override
                    public void onFinish(final ArrayList<String[]> list, final ArrayList<ArrayList<String[]>> files) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                interneterr.setVisibility(View.GONE);
                                refresh.setClickable(true);
                                
                                CustomAdapter adapter = new CustomAdapter(MainActivity.this, R.layout.list_layout, list, files);
                                listView.setAdapter(adapter);
                                
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView a, View v, int i, long l) {
                                        Uri uri = Uri.parse(list.get(i)[1]);
                                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(it);
                                    }
                                });
                            }
                        });
                    }
                    
                    @Override
                    public void onInternetError() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                interneterr.setVisibility(View.VISIBLE);
                                refresh.setClickable(true);
                            }
                        });
                    }
                });
        parser.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter iFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(completeReceiver, iFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(completeReceiver);
    }
}
