package me.blog.colombia2.schoolparser.parser;

import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import android.text.*;
import me.blog.colombia2.schoolparser.utils.*;

public class MenuTitleParser {
    private static Document doc = null;
    
    public static LinkedHashMap<String, LinkedHashMap<String, String>> getAllMenus(String schoolUrl) throws IOException {
        LinkedHashMap<String, LinkedHashMap<String, String>> menus = new LinkedHashMap<>();
        if(doc == null)
            doc = Jsoup.connect(schoolUrl+"/index.jsp")
                       .timeout(10*1000)
                       .data("mnu", "M001901").get();
        Elements maintitles = doc.getElementsByClass("cmaintitle");
        for(Element e : maintitles) {
            LinkedHashMap<String, String> submenus = new LinkedHashMap<>();
            Elements subtitles = e.getElementsByClass("csubtitle");
            for(Element menu : subtitles) {
                Element a = menu.select("a").first();
                String href = a.attr("href");
                submenus.put(a.text(), href.substring(href.lastIndexOf("=")+1, href.length()));
                
                if(href.substring(href.lastIndexOf("=")+1, href.length()).equals("M001002002")) {
                    submenus.put("청심학사 게시판", "M001002002001");
                    submenus.put("청심학사 Q&A", "M001002002002");
                    submenus.put("기숙형 고등학교란...", "M001002002003");
                    submenus.put("청심학사 소개", "M001002002004");
                    submenus.put("운영규정 및 생활규정", "M001002002005");
                    submenus.put("청심학사 일정표", "M001002002006");
                }
            }
            menus.put(e.select("a").first().text(), submenus);
        }
        /*
        Elements subtitles = doc.getElementsByClass("csubtitle");
        for(Element e : subtitles) {
            Element a = e.select("a").first();
            String href = a.attr("href");
            menus.put(a.text(), href.substring(href.lastIndexOf("=")+1, href.length()));
        }*/
        
        return menus;
    }
    
    public static ArrayList<ReplyData> getReplyList(String url) throws IOException {
        ArrayList<ReplyData> list = new ArrayList<>();
        Document article = Jsoup.connect(url)
                                .timeout(10*1000)
                                .get();
        Elements replies = article.getElementById("shortReply_view_form").select("ul");
        for(Element reply : replies) {
            String nickname = reply.select("li").get(0).text();
            Spanned content = Html.fromHtml(reply.select("li").get(1).toString());
            
            list.add(new ReplyData(nickname, content));
        }
        
        return list;
    }
}
