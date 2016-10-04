package me.blog.colombia2.schoolparser.parser;

import android.text.*;
import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import me.blog.colombia2.schoolparser.utils.*;

public class SchoolFoodParser {
    public static String getTable(int type) throws IOException {
        String result = "";
        
        String schYmd = DateInstance.YEAR+"."+getMonthFormat(DateInstance.MONTH)+"."+DateInstance.DATE;
        int currentDay = DateInstance.DAY;
        Document doc = Jsoup.connect("http://stu.cbe.go.kr/edusys.jsp?page=sts_m42310")
                            .timeout(10*1000)
                            .data("schulCode", "M100001915")
                            .data("schulCrseScCode", "4")
                            .data("schulKndScCode", "04")
                            .data("schYmd", schYmd)
                            .data("schMmealScCode", type+"")
                            .get();
        Elements foodList = doc.select("tbody tr").get(1).select("td");
        result = (Html.fromHtml(foodList.get(currentDay).toString())+"").replaceAll("[①-⑬0-9]", "");
        if(result.equals(""))
            result = "급식 정보가 없습니다.";
        
        return result; 
    }
    
    private static String getMonthFormat(int month) {
        String m = String.valueOf(month);
        if(m.length() == 1)
            m = "0"+m;
        return m;
    }
}
