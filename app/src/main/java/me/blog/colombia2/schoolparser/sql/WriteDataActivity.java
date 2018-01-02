package me.blog.colombia2.schoolparser.sql;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import me.blog.colombia2.schoolparser.MainActivity;
import me.blog.colombia2.schoolparser.R;
import me.blog.colombia2.schoolparser.sql.ArticleSQLManager;
import me.blog.colombia2.schoolparser.tab.SQLBoardFragment;

public class WriteDataActivity extends AppCompatActivity {
    protected int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mode = getIntent().getIntExtra("mode", 0);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        final EditText title = (EditText) findViewById(R.id.article_title);
        final EditText content = (EditText) findViewById(R.id.article_content);
        final EditText name = (EditText) findViewById(R.id.article_name);
        final EditText pass = (EditText) findViewById(R.id.article_pass);

        if(mode == 1) {
            getSupportActionBar().setTitle("댓글 작성");
            title.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.write_title)).setText("댓글 작성");
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Write Article
                if(mode == 0) {
                    if (title.getText().toString().equals("")
                            || content.getText().toString().equals("")
                            || name.getText().toString().equals("")
                            || pass.getText().toString().equals("")) {
                        Snackbar.make(view, "모든 정보를 입력해주세요", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final boolean result = ArticleSQLManager.writeArticle(name.getText().toString(), pass.getText().toString(), title.getText().toString(), content.getText().toString());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if(result) {
                                        MainActivity.instance.taskCurrent();
                                        finish();
                                    } else
                                        Snackbar.make(view, "게시글 등록에 실패했습니다", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();
                } else if(mode == 1) {
                    if (content.getText().toString().equals("")
                            || name.getText().toString().equals("")
                            || pass.getText().toString().equals("")) {
                        Snackbar.make(view, "모든 정보를 입력해주세요", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final boolean result = ArticleSQLManager.writeComment(getIntent().getIntExtra("article_id", -1), name.getText().toString(), pass.getText().toString(), content.getText().toString());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if(result) {
                                        SQLReplyActivity.instance.task();
                                        finish();
                                    } else
                                        Snackbar.make(view, "댓글 등록에 실패했습니다", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
