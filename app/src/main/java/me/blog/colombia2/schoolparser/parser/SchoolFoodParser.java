package me.blog.colombia2.schoolparser.parser;

import me.blog.colombia2.schoolparser.utils.*;

public class SchoolFoodParser {
    public static String getTable(int type) {
        String result = "";

        String[] resultArr = MealLibrary.getMealNew("cbe.go.kr", "M100001915", "4", "04", Integer.toString(type, 10), String.format("%d", DateInstance.YEAR), String.format("%02d", DateInstance.MONTH), String.format("%02d", DateInstance.DATE));
		try {
            result = resultArr[DateInstance.DAY];
		} catch(Exception e) {
			result = "급식 정보가 없습니다";
		}

		if(result.replaceAll("\\p{Z}","").equals(""))
		    result = "급식 정보가 없습니다";
        return result.replaceAll("[0-9\\.]", "");
    }
}
