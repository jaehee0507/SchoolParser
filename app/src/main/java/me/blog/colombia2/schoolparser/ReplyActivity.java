package me.blog.colombia2.schoolparser;

import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;

public class ReplyActivity extends AppCompatActivity {
    protected String url;
    protected LinearLayout replyList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replies);
        
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.reply_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        replyList = (LinearLayout) findViewById(R.id.replyList);
        
        if(savedInstanceState == null)
            url = getIntent().getStringExtra("hyperlink");
        else
            url = savedInstanceState.getString("url");
            
        new ReplyAsyncTask().execute();
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
    
    class ReplyAsyncTask extends AsyncTask<String, String, Integer> {
        private ArrayList<ReplyData> replies;
        
        @Override
        protected Integer doInBackground(String... p1) {
            try {
                replies = MenuTitleParser.getReplyList(url);
            } catch(Exception e) {
                return 1;
            }
            return 0;
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            if(result == 0) {
                for(ReplyData reply : replies) {
                    View card = LayoutInflater.from(ReplyActivity.this).inflate(R.layout.view_reply_form, null);
                    TextView nickname = (TextView) card.findViewById(R.id.nickname);
                    TextView content = (TextView) card.findViewById(R.id.content);
                    nickname.setText(reply.getNickname());
                    content.setText(reply.getContent());
                    replyList.addView(card);
                }
            } else if(result == 1) {
                
            }
            
            super.onPostExecute(result);
        }
    }
}
