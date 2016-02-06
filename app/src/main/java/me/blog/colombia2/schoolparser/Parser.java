package me.blog.colombia2.schoolparser;

import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class Parser {
    public interface OnParseFinishListener {
        public void onFinish(String category, int total, ArrayList<String[]> list, ArrayList<ArrayList<String[]>> files);
        public void onInternetError(Exception e);
    }
    
    private static String ORGIN_URL = "http://cw.hs.kr";
    
    private String url_old;
    private String url;
    private Document doc;
    private ArrayList<String[]> list;
    private ArrayList<ArrayList<String[]>> files;
    private OnParseFinishListener listener;
    protected int currentPage = 1;
    
    public Parser() {
        this("");
    }
    
    public Parser(String url) {
        this(url, null);
    }
    
    public Parser(String url, OnParseFinishListener listener) {
        this.url_old = url;
        this.url = ORGIN_URL+url+"&page="+currentPage;
        this.list = new ArrayList<>();
        this.files = new ArrayList<>();
        this.listener = listener;
    }
    
    public void setOnParseFinishListener(OnParseFinishListener listener) {
        this.listener = listener;
    }
    
    public void setUrl(String url) {
        this.url_old = url;
        this.url = ORGIN_URL+url+"&page="+currentPage;
    }
    
    public void setPage(int page) {
        this.currentPage = page;
        this.url = ORGIN_URL+url_old+"&page="+currentPage;
    }
    
    public void start() {
        this.list.clear();
        this.files.clear();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(url).timeout(10*1000).get();
                    String category_name = doc.select(".menuName").text().equals("") ? doc.getElementById("menuName").text() : doc.select(".menuName").text();
                    Elements articles = doc.select("tbody tr td .m_ltitle");
                    for(int i = 0; i < articles.size(); i++) {
                        Element e = articles.get(i);
                        
                        //Article's data
                        Element a = e.select("a").get(0);
                        if(a.select("span").size() > 0)
                            continue;
                        
                        Element date = doc.select("tbody tr td").get(i*5+3);
                        Elements img = e.select("img");
                        
                        String article_title = (a.attr("title").equals("") ? a.text() : a.attr("title"));
                        
                        Parser.this.list.add(new String[]{article_title, ORGIN_URL+a.attr("href"), date.text(), img.size() > 0 ? "1" : "0"});
                        
                        //Article's attachments
                        Elements attachs = doc.select("tbody tr td .m_limage").get(i).select("a");
                        ArrayList<String[]> attachList = new ArrayList<>();
                        for(Element attach : attachs) {
                            String title = attach.attr("title").replace(" 첨부파일 다운받기", "");
                            attachList.add(new String[]{title, ORGIN_URL+attach.attr("href")});
                        }
                        Parser.this.files.add(attachList);
                    }
                    int max_num = Integer.parseInt(doc.getElementById("m_total").select("dd").text().replace("건", ""), 10);
                    max_num = (int) Math.ceil((double) max_num / 10.0);
                    
                    Parser.this.listener.onFinish(category_name, max_num, Parser.this.list, Parser.this.files);
                } catch(Exception e) {
                    e.printStackTrace();
                    Parser.this.listener.onInternetError(e);
                }
            }
        }).start();
    }
}
