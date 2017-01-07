package me.blog.colombia2.schoolparser.parser;

import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.utils.*;
import org.hyunjun.school.*;

public class SchoolFoodParser {
    public static String getTable(int type) throws IOException {
        String result = "";
        
<<<<<<< HEAD
        School api = new School(School.Type.HIGH, School.Region.CHUNGBUK, "M100001915");
		try {
			ArrayList<SchoolMenu> list = new ArrayList<>(api.getMonthlyMenu(DateInstance.YEAR, DateInstance.MONTH));
			switch(type) {
				case 1:
					result = list.get(DateInstance.DATE).breakfast;
					break;
				case 2:
					result = list.get(DateInstance.DATE).lunch;
					break;
				case 3:
					result = list.get(DateInstance.DATE).dinner;
					break;
			}
		} catch(SchoolException e) {
			result = "급식 정보가 없습니다";
		}
		
        return result.replaceAll("[0-9\\.]", "").replace("급식이 없습니다", "급식 정보가 없습니다");
=======
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
>>>>>>> branch 'master' of https://github.com/affogatoman/SchoolParser.git
    }
}
