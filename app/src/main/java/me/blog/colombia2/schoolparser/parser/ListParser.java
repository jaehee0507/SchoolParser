package me.blog.colombia2.schoolparser.parser;

import org.jsoup.*;
import org.jsoup.select.*;
import org.jsoup.nodes.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import android.util.Log;

public class ListParser {
    final public static int CONNECT_TIMEOUT = 10;
    
    protected Document doc;
    
    /**
     * 학교홈페이지의 URL
     */
    protected String schoolUrl;
    
    /**
     * 파싱하는 메뉴의 ID
     */
    protected String menuId;
    
    /**
     * 현재 페이지
     */
    protected Integer currentPage;
    
    /**
     * 공지 필터링
     */
    protected Boolean filterNotice;
    
    public ListParser(String schoolUrl, String menuId) {
        this.schoolUrl = schoolUrl;
        this.menuId = menuId;
        
        init();
    }
    
    protected void init() {
        if(this.currentPage == null)
            this.currentPage = 1;
        if(this.filterNotice == null)
            this.filterNotice = false;
        
        connect();
    }

    protected void connect() {
        try {
            doc = Jsoup.connect(schoolUrl + "/index.jsp")
                .timeout(CONNECT_TIMEOUT * 1000)
                .data("mnu", menuId)
                .data("page", currentPage + "").get();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isFilteringNotice() {
        return this.filterNotice;
    }
    
    public void setFilteringNotice(boolean filterNotice) {
        this.filterNotice = filterNotice;
    }
    
    public void setCurrentPage(int page) {
        this.currentPage = page;
        
        connect();
    }
    
    public int getCurrentPage() {
        return this.currentPage;
    }
    
    public void setMenuId(String menuId) {
        this.menuId = menuId;
        
        init();
    }
    
    public String getMenuId() {
        return this.menuId;
    }
    
    public void setSchoolUrl(String schoolUrl) {
        this.schoolUrl = schoolUrl;
        
        init();
    }
    
    public String getSchoolUrl() {
        return this.schoolUrl;
    }

    public ArrayList<ArticleData> getArticleList() {
        Elements articles = doc.select("tbody tr");
        ArrayList<ArticleData> articleList = new ArrayList<>();
        for(int i = 0; i < articles.size(); i++) {
            Element article = articles.get(i);
            //if it is empty background
            if(article.select("td").size() < 4)
                continue;
            
            Element titleData = article.select("td").get(1).getElementsByClass("m_ltitle").first();
            boolean isNotice = titleData.select("a span").size() > 0;
            //if filtering notice is true and article is notice
            if((filterNotice && isNotice) || (currentPage > 1 && isNotice))
                continue;
            String date = article.select("td").get(3).text();
            String writer = article.select("td").get(2).text();
            int visitorCount = Integer.parseInt(article.select("td").get(4).text(), 10);
            String title = !isNotice ? titleData.select("a").first().text() : titleData.select("a span").first().text();
            String hyperLink = titleData.select("a").first().attr("href");
            
            ArrayList<FileData> attachments = new ArrayList<>();
            Elements attachElems = article.select(".m_limage a");
            for(Element file : attachElems) {
                String file_title = file.attr("title").replace(" 첨부파일 다운받기", "");
                String file_hyperLink = file.attr("href");
                attachments.add(new FileData(file_title, schoolUrl+file_hyperLink));
            }
            
            articleList.add(new ArticleData(title, date, writer, schoolUrl+hyperLink, visitorCount, isNotice, attachments));
        }
        
        return articleList;
    }
    
    public int getTotalArticles() {
        return Integer.parseInt(doc.getElementById("m_total").select("dd").first().text().replace("건", ""), 10);
    }
    
    public String getTitle() {
        return doc.select(".menuName").text().equals("") ? doc.getElementById("menuName").text() : doc.select(".menuName span").text();
    }
    
    /**
     * Caution; Slow Speed
     */
    public ArrayList<ArticleData> getAllArticles() {
        int original_page = getCurrentPage();
        int totalPages = (int) Math.ceil((double) getTotalArticles() / 10.0);
        
        ArrayList<ArticleData> result = new ArrayList<>();
        for(int i = 1; i <= totalPages; i++) {
            setCurrentPage(i);
            result.addAll(getArticleList());
        }
        
        setCurrentPage(original_page);
        return result;
    }
}
