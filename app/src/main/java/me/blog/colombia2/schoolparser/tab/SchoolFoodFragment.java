package me.blog.colombia2.schoolparser.tab;

import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import me.blog.colombia2.schoolparser.*;
import me.blog.colombia2.schoolparser.parser.*;
import java.io.*;
import android.content.*;
import java.util.*;

public class SchoolFoodFragment extends Fragment {
    protected TextView breakfast;
    protected TextView lunch;
    protected TextView dinner;
    
    public SchoolFoodFragment() {
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.school_food, container, false);
        breakfast = (TextView) layout.findViewById(R.id.breakfast);
        lunch = (TextView) layout.findViewById(R.id.lunch);
        dinner = (TextView) layout.findViewById(R.id.dinner);
        
        new SchoolFoodAsyncTask().execute();
        
        return layout;
    }
    
    public String getMenus() {
        Calendar c = Calendar.getInstance();
        String date = c.get(Calendar.YEAR)+"년 "+(c.get(Calendar.MONTH)+1)+"월 "+c.get(Calendar.DATE)+"일의 급식";
        return date+"\n \n"+
               "<조식>\n"+
               breakfast.getText().toString()+"\n"+
               "<중식>\n"+
               lunch.getText().toString()+"\n"+
               "<석식>\n"+
               dinner.getText().toString();
    }
    
    public boolean isLoading() {
        return dinner.getText().toString().equals(getResources().getString(R.string.loading));
    }
    
    class SchoolFoodAsyncTask extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            breakfast.setText(getResources().getString(R.string.loading));
            lunch.setText(getResources().getString(R.string.loading));
            dinner.setText(getResources().getString(R.string.loading));
        }

        @Override
        protected Integer doInBackground(String[] p1) {
            try {
                String breakfast_ = SchoolFoodParser.getTable(1);
                publishProgress("1", breakfast_);
                if(breakfast_.equals("급식이 없습니다.")) {
                    publishProgress("2", breakfast_);
                    publishProgress("2", breakfast_);
                    return null;
                }
                String lunch_ = SchoolFoodParser.getTable(2);
                publishProgress("2", lunch_);
                String dinner_ = SchoolFoodParser.getTable(3);
                publishProgress("3", dinner_);
            } catch(IOException e) {
                
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            
            if(values[0].equals("1"))
                breakfast.setText(values[1]);
            else if(values[0].equals("2"))
                lunch.setText(values[1]);
            else if(values[0].equals("3"))
                dinner.setText(values[1]);
        }
    }
}
