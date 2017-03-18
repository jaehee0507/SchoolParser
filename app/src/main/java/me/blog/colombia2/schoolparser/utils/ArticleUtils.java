package me.blog.colombia2.schoolparser.utils;

import java.net.*;
import org.jsoup.*;
import java.io.*;

public class ArticleUtils {
    public static int getSubMenuCount(String url) throws IOException {
        int i = 0;
        while(true) {
            if(Jsoup.connect(url+String.format("%03d", i+1)).get().select("title").first().text().equals("청원고등학교 홈페이지입니다."))
                break;
            else
                i++;
        }
        return i;
    }
}
