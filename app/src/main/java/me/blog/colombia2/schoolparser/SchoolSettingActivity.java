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
        SharedPreferences pref = getSharedPreferences("schoolData", MODE_PRIVATE);
        if(!pref.getString("menulist", "null").equals("null")) {
            SharedConstants.MENUS = new ArrayList<String>(Arrays.asList(pref.getString("menulist", "").split(";")));
            SharedConstants.MENU_NAMES = new ArrayList<String>(Arrays.asList(pref.getString("menunames", "").split(";")));
            
            changeActivity();
            return;
        }
        
        setContentView(R.layout.school_setting);
        
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
                SharedConstants.MENUS.clear();
                SharedConstants.MENU_NAMES.clear();
                    
                int checkedCount = 0;
                for(int i = 0; i < menulist.getChildCount(); i++) {
                    CheckBox child = (CheckBox) menulist.getChildAt(i);
                    if(child.isChecked()) {
                        checkedCount++;
                        SharedConstants.MENUS.add(menus.get(child.getText().toString()));
                        SharedConstants.MENU_NAMES.add(child.getText().toString());
                    }
                }
                
                if(checkedCount > 0) {
                    StringBuilder menulist = new StringBuilder();
                    StringBuilder menunames = new StringBuilder();
                    for(int i = 0; i < SharedConstants.MENUS.size(); i++) {
                        menulist.append(SharedConstants.MENUS.get(i)).append(";");
                        menunames.append(SharedConstants.MENU_NAMES.get(i)).append(";");
                    }
                    SharedPreferences.Editor edit = getSharedPreferences("schoolData", Context.MODE_PRIVATE).edit();
                    edit.putString("menulist", menulist.toString());
                    edit.putString("menunames", menunames.toString());
                    edit.commit();
                    
                    changeActivity();
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
        }
        return super.onOptionsItemSelected(item);
    }
    
    protected  void changeActivity() {
        Intent i = new Intent(SchoolSettingActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
    
    protected CheckBox getCheckBox(String key) {
        CheckBox checkbox = new CheckBox(this);
        checkbox.setText(key);
        return checkbox;
    }
}
