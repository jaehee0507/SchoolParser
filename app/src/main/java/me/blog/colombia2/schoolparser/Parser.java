package me.blog.colombia2.schoolparser;

import java.lang.*;
import java.net.*;
import java.io.*;
import java.util.*;

import org.jsoup.nodes.*;
import org.jsoup.*;
import org.jsoup.select.*;

public class Parser {
    public interface onParseFinishListener {
        public void onFinish(ArrayList<String[]> list, ArrayList<ArrayList<String[]>> files);
        public void onInternetError();
    }
    
    private static String ORGIN_URL = "http://cw.hs.kr";
    
    private String url;
    private Document doc;
    private ArrayList<String[]> list;
    private ArrayList<ArrayList<String[]>> files;
    private onParseFinishListener listener;
    
    public Parser(String url, onParseFinishListener listener) {
        this.url = url;
        this.list = new ArrayList<>();
        this.files = new ArrayList<>();
        this.listener = listener;
    }
    
    public void start() {
        this.list.clear();
        this.files.clear();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(url).get();
                    Elements articles = doc.select("td .m_ltitle");
                    for(int i = 0; i < articles.size(); i++) {
                        Element e = articles.get(i);
                        
                        //Article's data
                        Element a = e.select("a").get(0);
                        Element date = doc.select("tbody tr td").get(i*5+3);
                        Elements img = e.select("img");
                        
                        Parser.this.list.add(new String[]{a.attr("title"), ORGIN_URL+a.attr("href"), date.text(), img.size() > 0 ? "1" : "0"});
                        
                        //Article's attachments
                        Elements attachs = doc.select("tbody tr td .m_limage").get(i).select("a");
                        ArrayList<String[]> attachList = new ArrayList<>();
                        for(Element attach : attachs) {
                            String title = attach.select("img").attr("alt");
                            attachList.add(new String[]{title, ORGIN_URL+attach.attr("href")});
                        }
                        Parser.this.files.add(attachList);
                    }
                    
                    Parser.this.listener.onFinish(Parser.this.list, Parser.this.files);
                } catch(MalformedURLException e) {
                    e.printStackTrace();
                } catch(IOException e) {
                    e.printStackTrace();
                    Parser.this.listener.onInternetError();
                }
            }
        }).start();
    }
}
