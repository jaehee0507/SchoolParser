package me.blog.colombia2.schoolparser;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.rey.material.drawable.*;
import com.rey.material.widget.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.utils.*;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rey.material.widget.CheckBox;

public class SchoolSettingActivity extends AppCompatActivity {
    protected Button bottomButton;
    protected TextView title;
    protected TextView menuInfo;
    protected LinearLayout menulayout;
    protected LinearLayout menulist;
    
    protected LinkedHashMap<String, LinkedHashMap<String, String>> menus;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
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
                                menulist.addView(getCardView(key, menus.get(key)));
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
                    LinearLayout menulayout = (LinearLayout) menulist.getChildAt(i).findViewById(R.id.menulayout);
                    for(int j = 0; j < menulayout.getChildCount(); j++) {
                        CompoundButton check;
                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                            check = (CheckBox) menulayout.getChildAt(j);
                        else
                            check = (AppCompatCheckBox) menulayout.getChildAt(j);
                        if(check.isChecked()) {
                            checkedCount++;
                            menuArr.add((String) check.getTag());
                            menuNameArr.add(check.getText().toString());
                        }
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
                    
                    MainActivity.instance.finish();
                    PreferencesActivity.instance.finish();
                    Intent i = new Intent(SchoolSettingActivity.this, MainActivity.class);
                    startActivity(i);
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
                LinearLayout menulayout = (LinearLayout) menulist.getChildAt(i).findViewById(R.id.menulayout);
                for(int j = 0; j < menulayout.getChildCount(); j++) {
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        CheckBox check = (CheckBox) menulayout.getChildAt(j);
                        check.setChecked(true);
                    } else {
                        AppCompatCheckBox check = (AppCompatCheckBox) menulayout.getChildAt(j);
                        check.setChecked(true);
                    }
                }
            }
        } else if(item.getItemId() == R.id.menudeselect_all) {
            for(int i = 0; i < menulist.getChildCount(); i++) {
                LinearLayout menulayout = (LinearLayout) menulist.getChildAt(i).findViewById(R.id.menulayout);
                for(int j = 0; j < menulayout.getChildCount(); j++) {
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        CheckBox check = (CheckBox) menulayout.getChildAt(j);
                        check.setChecked(false);
                    } else {
                        AppCompatCheckBox check = (AppCompatCheckBox) menulayout.getChildAt(j);
                        check.setChecked(false);
                    }
                }
            }
        } else if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    protected CardView getCardView(String name, HashMap<String, String> menus) {
        CardView card = (CardView) LayoutInflater.from(this).inflate(R.layout.view_menu_card, null);
        ((TextView) card.findViewById(R.id.title)).setText(name);
        LinearLayout layout = (LinearLayout) card.findViewById(R.id.menulayout);
		SharedPreferences pref = getSharedPreferences("schoolData", MODE_PRIVATE);
		ArrayList<String> menunames = new ArrayList<>(Arrays.asList(pref.getString("menunames", "").split(";")));
        for(String key : menus.keySet()) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                final CheckBox check = new CheckBox(this);
                //Fucking off private filed via reflection...
                try {
                    Field field = CheckBoxDrawable.class.getDeclaredField("mAnimDuration");
                    field.setAccessible(true);
                    field.setInt(check.getButtonDrawable(), 200);
                } catch(Exception e) {}
                check.setText(key);
                check.setGravity(Gravity.CENTER|Gravity.LEFT);
                check.setTag(menus.get(key));
			    if(menunames.contains(key))
				    check.setCheckedImmediately(true);
                layout.addView(check);
            } else {
                AppCompatCheckBox check = new AppCompatCheckBox(this);
                check.setText(key);
                check.setGravity(Gravity.CENTER|Gravity.LEFT);
                check.setTag(menus.get(key));
                if(menunames.contains(key))
                    check.setChecked(true);
                layout.addView(check);
            }
        }
        
        return card;
    }
}
