package me.blog.colombia2.schoolparser;

import android.support.v7.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.util.*;
import android.graphics.*;
import android.graphics.drawable.*;
import java.util.*;

import com.balysv.materialripple.MaterialRippleLayout;

public class AttachmentsActivity extends AppCompatActivity {
    protected LinearLayout main;
    protected HashMap<String, View> views;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        setContentView(main);
        
        views = new HashMap<>();
        
        ArrayList<String[]> files = SharedConstants.data;
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        for(String[] attach : files) {
            layout.addView(getCheckBoxLayout(attach));
            View view = new View(this);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(
                                                                                                             TypedValue.COMPLEX_UNIT_DIP,
                                                                                                             1,
                                                                                                             getResources().getDisplayMetrics())));
            view.setBackgroundColor(Color.rgb(200, 200, 200));
            layout.addView(view);
        }
        scroll.addView(layout);
        main.addView(scroll);
        
        Button button = new Button(this);
        button.setBackgroundDrawable(new ColorDrawable(Color.rgb(180, 180, 180)));
        MaterialRippleLayout ripple = MaterialRippleLayout.on(button)
                                        .rippleColor(Color.rgb(40, 40, 40))
                                        .rippleOverlay(true)
                                        .rippleAlpha(0.5f)
                                        .rippleDuration(200)
                                        .rippleFadeDuration(100)
                                        .create();
        main.addView(ripple);
    }
    
    private CheckBox getCheckBoxLayout(String[] attach) {
        CheckBox checkbox = new CheckBox(this);
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
        return checkbox;
    }
}
