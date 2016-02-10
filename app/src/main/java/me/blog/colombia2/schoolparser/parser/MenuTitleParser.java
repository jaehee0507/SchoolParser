package me.blog.colombia2.schoolparser.parser;

import java.io.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

public class MenuTitleParser {
    public static String getTitle(String schoolUrl, String menuId) throws IOException {
        Document doc = Jsoup.connect(schoolUrl+"/index.jsp").timeout(10*1000)
                        .data("mnu", menuId).get();
        String title = doc.select("title").first().text();
        return title.substring(0, title.indexOf("<")-1);
    }
}
