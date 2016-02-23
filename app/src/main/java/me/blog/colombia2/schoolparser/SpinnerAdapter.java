package me.blog.colombia2.schoolparser;

import android.widget.*;
import android.content.*;
import java.util.*;
import android.view.*;
import android.graphics.*;

public class SpinnerAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> items;
    
    public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        this.items = objects;
        this.context = context;
    }
    
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(items.get(position));
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(16);
        return convertView;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(items.get(position));
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(20);
        return convertView;
    }
}
