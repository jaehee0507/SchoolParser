package me.blog.colombia2.schoolparser.parser;

import org.jsoup.*;
import org.jsoup.select.*;
import org.jsoup.nodes.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ListParser {
    final public static int CONNECT_TIMEOUT = 10;
    
    protected Document doc;
    protected String schoolUrl;
    protected String menuId;
    protected int currentPage;
    
    public ListParser(String schoolUrl, String menuId) {
        this.schoolUrl = schoolUrl;
        this.menuId = menuId;
        
        init();
    }
    
    protected void init() {
        connect();
        
        this.currentPage = 1;
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
    
    public void setCurrentPage(int page) {
        this.currentPage = page;
    }
    
    public int getCurrentPage() {
        return this.currentPage;
    }
    
    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
    
    public String getMenuId() {
        return this.menuId;
    }
    
    public void setSchoolUrl(String schoolUrl) {
        this.schoolUrl = schoolUrl;
    }
    
    public String getSchoolUrl() {
        return this.schoolUrl;
    }

    public ArrayList<ArticleData> getArticleList() {
        Elements articles = doc.select("tbody tr");
        ArrayList<ArticleData> articleList = new ArrayList<>();
        for(int i = 0; i < articles.size(); i++) {
            Element article = articles.get(i);
            
            Element titleData = article.select("td").get(1).select(".m_ltitle").first();
            boolean isNotice = titleData.select("a span").size() > 0;
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
                attachments.add(new FileData(file_title, file_hyperLink));
            }
            
            articleList.add(new ArticleData(title,date, writer, hyperLink, visitorCount, isNotice, attachments));
        }
        
        return articleList;
    }
    
    public int getTotalArticles() {
        return Integer.parseInt(doc.select(".m_total dd").first().text().replace("건", ""), 10);
    }
}

class ArticleData {
    final protected String title;
    final protected String date;
    final protected String writer;
    final protected String hyperLink;
    final protected int visitorCount;
    final protected boolean isNotice;
    final protected ArrayList<FileData> attachments;
    
    public ArticleData(String title, String date, String writer, String hyperLink, int visitorCount, boolean isNotice, ArrayList<FileData> attachments) {
        this.title = title;
        this.date = date;
        this.writer = writer;
        this.hyperLink = hyperLink;
        this.visitorCount = visitorCount;
        this.isNotice = isNotice;
        this.attachments = attachments;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getDate() {
        return this.date;
    }
    
    public String getWriter() {
        return this.writer;
    }
    
    public String getHyperLink() {
        return this.hyperLink;
    }
    
    public int getVisitors() {
        return this.visitorCount;
    }
    
    public boolean isNotice() {
        return this.isNotice;
    }
    
    public ArrayList<FileData> getAttachments() {
        return this.attachments;
    }
}

class FileData {
    final protected String title;
    final protected String hyperLink;
    
    public FileData(String title, String hyperLink) {
        this.title = title;
        this.hyperLink = hyperLink;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getHyperLink() {
        return this.hyperLink;
    }
}
