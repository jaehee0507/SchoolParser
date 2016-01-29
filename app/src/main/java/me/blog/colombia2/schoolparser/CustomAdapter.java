package me.blog.colombia2.schoolparser;

import android.app.*;
import android.content.*;
import android.widget.*;
import android.view.*;
import android.util.*;
import android.net.*;
import android.graphics.*;
import java.util.*;

public class CustomAdapter extends BaseAdapter {
    private MainActivity activity;
    private LayoutInflater inflater;
    private ArrayList<String[]> data;
    private ArrayList<ArrayList<String[]>> files;
    private int layout;

    public CustomAdapter(MainActivity activity, int layout, ArrayList<String[]> data, ArrayList<ArrayList<String[]>> files){
        this.activity = activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
        this.files = files;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String[] getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = inflater.inflate(layout, parent, false);
        }
        
        TextView title = (TextView) convertView.findViewById(R.id.title);
        if(data.get(position)[3] == "1")
            title.setTextColor(Color.RED);
        else
            title.setTextColor(Color.BLACK);
        title.setText(data.get(position)[0]);
        
        TextView date = (TextView) convertView.findViewById(R.id.date);
        date.setText(data.get(position)[2]);
        
        Button file = (Button) convertView.findViewById(R.id.files);
        file.setFocusable(false);
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String[]> attach = files.get(position);
                createDownloadDialog(attach);
            }
        });
        
        return convertView;
    }
    
    private void createDownloadDialog(final ArrayList<String[]> attach) {
        try{
        Dialog dialog = new Dialog(activity);
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        for(int i = 0; i < attach.size(); i++) {
            final String[] arr = attach.get(i);
            LinearLayout l = new LinearLayout(activity);
            l.setOrientation(LinearLayout.HORIZONTAL);
            
            int eight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, activity.getResources().getDisplayMetrics());
            TextView tv = new TextView(activity);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            tv.setSingleLine(true);
            tv.setGravity(Gravity.CENTER|Gravity.LEFT);
            tv.setPadding(eight, 0, 0, 0);
            tv.setText(arr[0]);
            l.addView(tv);
            
            ImageButton download = new ImageButton(activity);
            download.setPadding(eight, eight, eight, eight);
            download.setImageResource(R.drawable.ic_file_download_black);
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, activity.getResources().getDisplayMetrics());
            download.setLayoutParams(new LinearLayout.LayoutParams(px, LinearLayout.LayoutParams.MATCH_PARENT));
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(arr[1]);
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(it);
                }
            });
            l.addView(download);
            
            layout.addView(l);
        }
        dialog.setContentView(layout);
        dialog.setTitle("첨부파일 다운로드");
        dialog.show();
        }catch(Exception e) {
            Log.e("affoparser", e+"");
        }
    }
}