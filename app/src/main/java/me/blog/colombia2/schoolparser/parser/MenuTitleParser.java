package me.blog.colombia2.schoolparser.parser;

import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import android.util.*;

public class MenuTitleParser {
    public static String getTitle(String schoolUrl, String menuId) throws IOException {
        Document doc = Jsoup.connect(schoolUrl+"/index.jsp").timeout(10*1000)
                        .data("mnu", menuId).get();
        String title = doc.select("title").first().text();
        return title.substring(0, title.indexOf("<")-1);
    }
    
    public static String getSchoolName(String schoolUrl) {
        return "";
    }
    
    public static HashMap<String, String> getAllMenus(String schoolUrl) throws IOException {
        HashMap<String, String> menus = new HashMap<>();
        Document doc = Jsoup.connect(schoolUrl+"/index.jsp").timeout(10*1000)
                        .data("mnu", "M001901").get();
        Elements subtitles = doc.getElementsByClass("csubtitle");
        for(Element e : subtitles) {
            Element a = e.select("a").first();
            String href = a.attr("href");
            menus.put(a.text(), href.substring(href.lastIndexOf("=")+1, href.length()));
        }
        
        return menus;
    }
}
