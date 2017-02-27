package me.blog.colombia2.schoolparser;

import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;
import me.blog.colombia2.schoolparser.utils.*;

public class PreferencesActivity extends AppCompatActivity {
    SwitchCompat autoupdate;
    SwitchCompat newvisible;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.school_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        TextView title = (TextView) findViewById(R.id.pref_title);
        title.setText("설정");
        TextView menuInfo = (TextView) findViewById(R.id.pref_info);
        menuInfo.setText("앱의 설정을 변경할 수 있습니다.");
        
        FloatingActionButton schoolsetting = (FloatingActionButton) findViewById(R.id.schoolsetting);
        schoolsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PreferencesActivity.this, SchoolSettingActivity.class);
                startActivity(i);
            }
        });
        
        FloatingActionButton appinfo = (FloatingActionButton) findViewById(R.id.appinfo);
        appinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popup = new PopupWindow(getBaseContext());
                LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.view_dev_info, null);
                layout.findViewById(R.id.cheong_logo).setAnimation(android.view.animation.AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale_anim));
                layout.findViewById(R.id.cheong_text).setAnimation(android.view.animation.AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in_anim));
                try {
                    ((TextView) layout.findViewById(R.id.cheong_text)).setText("2128 조재희\n \n앱 버전 : v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch(PackageManager.NameNotFoundException e) {}
                popup.setFocusable(true);
                popup.setContentView(layout);
                Point p = new Point();
                getWindowManager().getDefaultDisplay().getSize(p);
                popup.setWidth(p.x);
                popup.setHeight(p.y);
                popup.setBackgroundDrawable(new BitmapDrawable(ImageEditor.blur(getBaseContext(), ImageEditor.captureScreen(findViewById(R.id.main_layout).getRootView()), 25.0f)));
                popup.setAnimationStyle(R.style.PopupAnim);
                popup.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            }
        });
        
        autoupdate = (SwitchCompat) findViewById(R.id.autoupdate);
        final SharedPreferences pref = getSharedPreferences("appData", MODE_PRIVATE);
        autoupdate.setChecked(pref.getBoolean("autoUpdate", true));
        autoupdate.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean("autoUpdate", checked);
                edit.commit();
            }
        });
        
        newvisible = (SwitchCompat) findViewById(R.id.newvisible);
        SpannableString str = new SpannableString(newvisible.getText());
        Drawable d = getResources().getDrawable(R.drawable.newicon);
        d.setBounds(0, 0, newvisible.getLineHeight(), newvisible.getLineHeight());
        str.setSpan(new ImageSpan(d), 7, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        newvisible.setText(str);
        newvisible.setChecked(pref.getBoolean("newVisible", true));
        newvisible.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean("newVisible", checked);
                edit.commit();
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
