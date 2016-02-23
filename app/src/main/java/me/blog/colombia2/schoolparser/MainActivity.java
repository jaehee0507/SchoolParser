package me.blog.colombia2.schoolparser;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import me.blog.colombia2.schoolparser.tab.*;

public class MainActivity extends AppCompatActivity {
    public ViewPager viewPager;
    protected MenuPagerAdapter adapter;
    protected TabLayout tabLayout;
    protected ArrayList<String> menuArr;
    protected ArrayList<String> menuNameArr;
    protected Spinner menuSpinner;
    
    public static MainActivity instance;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        menuSpinner = (Spinner) findViewById(R.id.category_spinner);
        
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
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(MainActivity.this, SchoolSettingActivity.class);
                startActivity(i);
                
                return true;
            }
        });
        
        return true;
    }
    
    protected void startParser() {
        SharedPreferences pref = getSharedPreferences("schoolData", MODE_PRIVATE);
        if(!pref.getString("menulist", "null").equals("null")) {
            menuArr = new ArrayList<String>(Arrays.asList(pref.getString("menulist", "").split(";")));
            menuNameArr = new ArrayList<String>(Arrays.asList(pref.getString("menunames", "").split(";")));
        } else {
            Intent i = new Intent(MainActivity.this, SchoolSettingActivity.class);
            startActivity(i);
            return;
        }
        
        ArrayList<String> nameArr = new ArrayList<>();
        nameArr.add("급식 정보");
        nameArr.addAll(menuNameArr);
        SpinnerAdapter category = new SpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, nameArr);
        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView v, View a, int p, long l) {
                tabLayout.getTabAt(p).select();
                viewPager.setCurrentItem(p);
            }
            
            @Override
            public void onNothingSelected(AdapterView v) {
                
            }
        });
        menuSpinner.setAdapter(category);
        
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new MenuPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new SchoolFoodFragment(), "급식 정보");
        for(int i = 0; i < menuArr.size(); i++) {
            ArticlePageFragment frag = new ArticlePageFragment();
            frag.setMenuId(menuArr.get(i));
            adapter.addFragment(frag, menuNameArr.get(i));
        }

        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                menuSpinner.setSelection(tab.getPosition());
               // getSupportActionBar().setTitle(tab.getText());
                viewPager.setCurrentItem(tab.getPosition());
            }

           @Override
           public void onTabUnselected(TabLayout.Tab tab) {}

           @Override
           public void onTabReselected(TabLayout.Tab tab) {}
        });

        getSupportActionBar().setTitle(adapter.getPageTitle(viewPager.getCurrentItem()));
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
}
