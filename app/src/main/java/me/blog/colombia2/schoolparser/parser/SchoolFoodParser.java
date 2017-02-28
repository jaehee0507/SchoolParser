package me.blog.colombia2.schoolparser.parser;

import java.io.*;
import java.util.*;
import me.blog.colombia2.schoolparser.utils.*;
import org.hyunjun.school.*;

public class SchoolFoodParser {
    public static String getTable(int type) {
        String result = "";
        
        School api = new School(School.Type.HIGH, School.Region.CHUNGBUK, "M100001915");
		try {
			ArrayList<SchoolMenu> list = new ArrayList<>(api.getMonthlyMenu(DateInstance.YEAR, DateInstance.MONTH));
			switch(type) {
				case 1:
					result = list.get(DateInstance.DATE-1).breakfast;
					break;
				case 2:
					result = list.get(DateInstance.DATE-1).lunch;
					break;
				case 3:
					result = list.get(DateInstance.DATE-1).dinner;
					break;
			}
		} catch(SchoolException e) {
			result = "급식 정보가 없습니다";
		}
		
        return result.replaceAll("[0-9\\.]", "").replace("급식이 없습니다", "급식 정보가 없습니다");
    }
}
