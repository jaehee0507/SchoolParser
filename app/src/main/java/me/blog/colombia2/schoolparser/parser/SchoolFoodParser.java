package me.blog.colombia2.schoolparser.parser;

import android.util.*;
import java.io.*;
import java.text.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import android.text.*;

public class SchoolFoodParser {
    public static String getTable(int type) throws IOException {
        String result = "";
        
        Calendar c = Calendar.getInstance();
        String schYmd = c.get(Calendar.YEAR)+"."+getMonthFormat(c.get(Calendar.MONTH))+"."+c.get(Calendar.DATE);
        int currentDay = c.get(Calendar.DAY_OF_WEEK)-1;
        Document doc = Jsoup.connect("http://hes.cbe.go.kr/sts_sci_md01_001.do")
                            .timeout(10*1000)
                            .data("schulCode", "M100001915")
                            .data("schulCrseScCode", "4")
                            .data("schulKndScCode", "04")
                            .data("schYmd", schYmd)
                            .data("schMmealScCode", type+"")
                            .get();
        Elements foodList = doc.select("tbody tr").get(1).select("td");
        result = (Html.fromHtml(foodList.get(currentDay).toString())+"").replaceAll("[①-⑬]", "");
        if(result.equals(" "))
            result = "급식이 없습니다.";
        
        return result; 
    }
    
    private static String getMonthFormat(int month) {
        String m = String.valueOf(month+1);
        if(m.length() == 1)
            m = "0"+m;
        return m;
    }
}
