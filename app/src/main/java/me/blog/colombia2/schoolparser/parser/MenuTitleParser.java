package me.blog.colombia2.schoolparser.parser;

import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class MenuTitleParser {
    private static Document doc = null;
    
    public static HashMap<String, String> getAllMenus(String schoolUrl) throws IOException {
        HashMap<String, String> menus = new HashMap<>();
        if(doc == null)
            doc = Jsoup.connect(schoolUrl+"/index.jsp")
                       .timeout(10*1000)
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
