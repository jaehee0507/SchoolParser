package me.blog.colombia2.schoolparser;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.utils.*;

public class SchoolSettingActivity extends AppCompatActivity {
    protected Button bottomButton;
    protected TextView title;
    protected TextView menuInfo;
    protected LinearLayout menulayout;
    protected LinearLayout menulist;
    
    protected HashMap<String, String> menus;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_setting);
        
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.school_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        menulayout = (LinearLayout) findViewById(R.id.menulayout);
        menulist = (LinearLayout) findViewById(R.id.menulist);
        
        title = (TextView) findViewById(R.id.title);
        title.setText("게시판 선택하기");
        menuInfo = (TextView) findViewById(R.id.menuinfo);
        String source = "게시판을 선택해주세요.\n"+
                        "최소한 한 개는 선택하셔야 합니다.";
        menuInfo.setText(source);

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    menus = MenuTitleParser.getAllMenus(SharedConstants.SCHOOL_URL);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            for(String key : menus.keySet())
                                menulist.addView(getCheckBox(key));
                        }
                    });
                } catch(IOException e) {
                    
                }
            }
        }).start();
        
        bottomButton = (Button) findViewById(R.id.bottom);
        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("schoolData", MODE_PRIVATE).edit().clear().commit();
                final ArrayList<String> menuArr = new ArrayList<>();
                final ArrayList<String> menuNameArr = new ArrayList<>();
                    
                int checkedCount = 0;
                for(int i = 0; i < menulist.getChildCount(); i++) {
                    CheckBox child = (CheckBox) menulist.getChildAt(i);
                    if(child.isChecked()) {
                        checkedCount++;
                        menuArr.add(menus.get(child.getText().toString()));
                        menuNameArr.add(child.getText().toString());
                    }
                }
                
                if(checkedCount > 0) {
                    StringBuilder menulist = new StringBuilder();
                    StringBuilder menunames = new StringBuilder();
                    for(int i = 0; i < menuArr.size(); i++) {
                        menulist.append(menuArr.get(i)).append(";");
                        menunames.append(menuNameArr.get(i)).append(";");
                    }
                    SharedPreferences.Editor edit = getSharedPreferences("schoolData", MODE_PRIVATE).edit();
                    edit.putString("menulist", menulist.toString());
                    edit.putString("menunames", menunames.toString());
                    edit.commit();
                    
                    MainActivity.instance.startParser();
                    finish();
                } else
                    Snackbar.make(getWindow().getDecorView(), "게시판 한 개 이상을 선택해주세요.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schoolsettings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menuselect_all) {
            for(int i = 0; i < menulist.getChildCount(); i++) {
                CheckBox child = (CheckBox) menulist.getChildAt(i);
                child.setChecked(true);
            }
        } else if(item.getItemId() == R.id.menudeselect_all) {
            for(int i = 0; i < menulist.getChildCount(); i++) {
                CheckBox child = (CheckBox) menulist.getChildAt(i);
                child.setChecked(false);
            }
        } else if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    protected CheckBox getCheckBox(String key) {
        CheckBox checkbox = new CheckBox(this);
        checkbox.setText(key);
        return checkbox;
    }
}
