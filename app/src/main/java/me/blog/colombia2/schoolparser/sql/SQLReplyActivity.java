package me.blog.colombia2.schoolparser.sql;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import me.blog.colombia2.schoolparser.R;
import me.blog.colombia2.schoolparser.parser.MenuTitleParser;
import me.blog.colombia2.schoolparser.parser.ReplyData;
import me.blog.colombia2.schoolparser.utils.ArticleUtils;
import me.blog.colombia2.schoolparser.utils.ErrorDisplayer;

public class SQLReplyActivity extends AppCompatActivity {
    protected int article_id;
    protected LinearLayout replyList;

    public static SQLReplyActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlreply);

        if(savedInstanceState == null)
            article_id = getIntent().getIntExtra("article_id", 0);
        else
            article_id = savedInstanceState.getInt("article_id");

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.sqlreply_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        replyList = (LinearLayout) findViewById(R.id.replyList);

        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SQLReplyActivity.this, WriteDataActivity.class);
                i.putExtra("article_id", article_id);
                i.putExtra("mode", 1);
                startActivity(i);
            }
        });

        task();
    }

    public void task() {
        replyList.removeAllViews();
        new ReplyAsyncTask().execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("article_id", article_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sqlreplies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        } else if(item.getItemId() == R.id.refresh) {
            replyList.removeAllViews();
            new ReplyAsyncTask().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    class ReplyAsyncTask extends AsyncTask<String, String, Integer> {
        private ArrayList<SQLReply> replies;

        @Override
        protected Integer doInBackground(String... p1) {
            try {
                replies = ArticleSQLManager.getReplies(article_id);
            } catch(Exception e) {
                return 1;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result == 0) {
                for(final SQLReply reply : replies) {
                    View card = LayoutInflater.from(SQLReplyActivity.this).inflate(R.layout.view_reply_form, null);
                    TextView nickname = (TextView) card.findViewById(R.id.nickname);
                    TextView content = (TextView) card.findViewById(R.id.content);
                    nickname.setText(reply.getName());
                    content.setText(reply.getContent() + "\n \n" + reply.getWdate() + " | " + reply.getIp().substring(0, reply.getIp().lastIndexOf("."))+".xxx");
                    card.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(final View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setTitle("비밀번호 입력");
                            builder.setMessage("댓글을 삭제하려면 댓글 비밀번호를 입력해주세요.");
                            final AppCompatEditText editText = new AppCompatEditText(v.getContext());
                            editText.setHint("비밀번호");
                            builder.setView(editText);
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(reply.getPass().equals(ArticleUtils.sha1(editText.getText().toString()))) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                final boolean result = ArticleSQLManager.deleteComment(article_id, reply.getOrder());
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if(result)
                                                            task();
                                                        else
                                                            ErrorDisplayer.showError(v, "댓글 삭제에 실패했습니다");
                                                    }
                                                });
                                            }
                                        }).start();
                                    } else {
                                        ErrorDisplayer.showError(v, "비밀번호가 일치하지 않습니다");
                                    }
                                }
                            });
                            builder.setNegativeButton("취소", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            return false;
                        }
                    });
                    replyList.addView(card);
                }
            } else if(result == 1) {

            }

            super.onPostExecute(result);
        }
    }
}
