package me.blog.colombia2.schoolparser;

import android.support.v7.app.*;
import android.support.v4.widget.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.util.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.support.design.widget.*;
import java.util.*;
import java.io.*;
import java.net.*;

import com.balysv.materialripple.MaterialRippleLayout;

public class AttachmentsActivity extends AppCompatActivity {
    protected HashMap<RelativeLayout, String> views;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attachments);
        
        views = new HashMap<>();
        
        ArrayList<String[]> files = SharedConstants.data;
        LinearLayout layout = (LinearLayout) findViewById(R.id.content_layout);
        for(String[] attach : files) {
            RelativeLayout checkbox = getCheckBoxLayout(attach);
            views.put(checkbox, attach[1]);
            layout.addView(checkbox);
            View view = new View(this);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(
                                                                                                             TypedValue.COMPLEX_UNIT_DIP,
                                                                                                             1,
                                                                                                             getResources().getDisplayMetrics())));
            view.setBackgroundColor(Color.rgb(200, 200, 200));
            layout.addView(view);
        }
        
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<RelativeLayout> keys = views.keySet();
                for(RelativeLayout layout : keys) {
                    final CheckBox checkbox = (CheckBox) layout.findViewById(0);
                    if(!checkbox.isChecked())
                        continue;
                    
                    final String value = views.get(checkbox);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BufferedInputStream bis = new BufferedInputStream(new URL(value).openStream());
                                File target = new File("/sdcard/Download/"+checkbox.getText().toString());
                                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
                                
                                byte[] bytes = new byte[4096];
                                while(bis.read(bytes, 0, 4096) != -1)
                                    bos.write(bytes, 0, 4096);
                                    
                                bis.close();
                                bos.flush();
                                bos.close();
                                
                                checkbox.setText(checkbox.getText()+" V");
                            } catch(Exception e) {
                                
                            }
                        }
                    }).start();
                }
            }
        });
    }
    
    private RelativeLayout getCheckBoxLayout(String[] attach) {
        RelativeLayout layout = new RelativeLayout(this);
        CheckBox checkbox = new CheckBox(this);
        checkbox.setId(0);
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(Color.argb(20, 0, 0, 0)));
        states.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(Color.argb(0, 0, 0, 0)));
        states.addState(new int[]{}, new ColorDrawable(Color.argb(0, 0, 0, 0)));
        checkbox.setBackgroundDrawable(states);
        checkbox.setText(attach[0]);
        checkbox.setGravity(Gravity.LEFT|Gravity.CENTER);
        checkbox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(
                                                                                                             TypedValue.COMPLEX_UNIT_DIP,
                                                                                                             60,
                                                                                                             getResources().getDisplayMetrics())));
        layout.addView(checkbox);
        return layout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attachments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.select_all) {
            Set<RelativeLayout> keys = views.keySet();
            for(RelativeLayout layout : keys) {
                CheckBox checkbox = (CheckBox) layout.findViewById(0);
                checkbox.setChecked(true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
