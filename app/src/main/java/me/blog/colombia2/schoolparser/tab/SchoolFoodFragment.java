package me.blog.colombia2.schoolparser.tab;

import android.os.*;
import android.support.v4.app.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.wdullaer.materialdatetimepicker.date.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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

                                dateselect.setText(DateInstance.YEAR + "년 " + DateInstance.MONTH + "월 " + DateInstance.DATE + "일");
                                breakfast.setText(getResources().getString(R.string.loading));
                                lunch.setText(getResources().getString(R.string.loading));
                                dinner.setText(getResources().getString(R.string.loading));
                                new SchoolFoodAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1);
                                new SchoolFoodAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 2);
                                new SchoolFoodAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 3);
                            }
                        },
                        DateInstance.YEAR,
                        DateInstance.MONTH - 1,
                        DateInstance.DATE
                    );
                    dpd.show(MainActivity.instance.getFragmentManager(), "Datepickerdialog");
                }
            });

        dateselect.setText(DateInstance.YEAR + "년 " + DateInstance.MONTH + "월 " + DateInstance.DATE + "일");
        breakfast.setText(getResources().getString(R.string.loading));
        lunch.setText(getResources().getString(R.string.loading));
        dinner.setText(getResources().getString(R.string.loading));
        new SchoolFoodAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1);
        new SchoolFoodAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 2);
        new SchoolFoodAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 3);

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
            Log.e("affocheong", new Boolean(dinner==null).toString());
            return dinner.getText().toString().equals(getResources().getString(R.string.loading));
        } catch(Exception e) {
            Log.e("affocheong", e.toString());
            return true;
        }
    }

    class SchoolFoodAsyncTask extends AsyncTask<Integer, String, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Integer doInBackground(Integer[] args) {
            try {
                Document doc = Jsoup.connect("http://stu.cbe.go.kr/sts_sci_md01_001.do?schulCode=M100001915&schulCrseScCode=4")
                                    .data("schMmealScCode", args[0].toString())
                                    .data("schYmd", DateInstance.YEAR+"."+String.format("%02d", DateInstance.MONTH)+"."+String.format("%02d", DateInstance.DATE))
                                    .timeout(10 * 1000).get();
                Elements elements = doc.getElementsByClass("textC");
                String data = elements.get(DateInstance.DAY+7).text().replaceAll("[0-9\\.]", "").replace(" ", "\n");
                if(data.replaceAll("\\s", "").equals(""))
                    publishProgress(args[0].toString(), "급식 정보가 없습니다");
                else
                    publishProgress(args[0].toString(), data);
            } catch(Exception e) {
                Log.e("affocheong", e.toString());
                publishProgress(args[0].toString(), "급식 정보가 없습니다");
                ErrorDisplayer.showError(breakfast, "현재 급식을 불러올 수 없습니다");
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
