package me.blog.colombia2.schoolparser.tab;

import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import com.wdullaer.materialdatetimepicker.date.*;
import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.*;
import me.blog.colombia2.schoolparser.parser.*;
import me.blog.colombia2.schoolparser.utils.*;

public class SchoolFoodFragment extends Fragment {
    protected TextView breakfast;
    protected TextView lunch;
    protected TextView dinner;
	protected TextView dateselect;

    public SchoolFoodFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.view_school_food, container, false);
        breakfast = (TextView) layout.findViewById(R.id.breakfast);
        lunch = (TextView) layout.findViewById(R.id.lunch);
        dinner = (TextView) layout.findViewById(R.id.dinner);
		dateselect = (TextView) layout.findViewById(R.id.dateselect);

		dateselect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog dialog, int y, int m, int d) {
                                DateInstance.YEAR = y;
                                DateInstance.MONTH = m + 1;
                                DateInstance.DATE = d;
                                Calendar c = Calendar.getInstance();
                                c.set(y, m, d);
                                DateInstance.DAY = c.get(Calendar.DAY_OF_WEEK) - 1;

                                new SchoolFoodAsyncTask().execute();
                            }
                        },
                        DateInstance.YEAR,
                        DateInstance.MONTH - 1,
                        DateInstance.DATE
                    );
                    dpd.show(MainActivity.instance.getFragmentManager(), "Datepickerdialog");
                }
            });

        new SchoolFoodAsyncTask().execute();

        return layout;
    }

    public String getMenus() {
        String date = DateInstance.YEAR + "년 " + DateInstance.MONTH + "월 " + DateInstance.DATE + "일의 급식";
        return date + "\n \n" +
            "<조식>\n" +
            breakfast.getText().toString() + "\n" +
            "<중식>\n" +
            lunch.getText().toString() + "\n" +
            "<석식>\n" +
            dinner.getText().toString();
    }

    public boolean isLoading() {
        try {
            return dinner.getText().toString().equals(getResources().getString(R.string.loading));
        } catch(Exception e) {
            return true;
        }
    }

    class SchoolFoodAsyncTask extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

			dateselect.setText(DateInstance.YEAR + "년 " + DateInstance.MONTH + "월 " + DateInstance.DATE + "일");
            breakfast.setText(getResources().getString(R.string.loading));
            lunch.setText(getResources().getString(R.string.loading));
            dinner.setText(getResources().getString(R.string.loading));
        }

        @Override
        protected Integer doInBackground(String[] p1) {
            try {
                String breakfast_ = SchoolFoodParser.getTable(1);
                publishProgress("1", breakfast_);
                if(breakfast_.equals("급식 정보가 없습니다")) {
                    publishProgress("2", breakfast_);
                    publishProgress("3", breakfast_);
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
