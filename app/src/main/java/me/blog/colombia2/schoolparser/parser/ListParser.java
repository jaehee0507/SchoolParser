package me.blog.colombia2.schoolparser.parser;

import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class ListParser {
    final private static int CONNECT_TIMEOUT = 10;
    
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
    
    /**
     * Singleton patterned Object
     */
    private static ListParser singleton_instance = new ListParser();
    
    private ListParser() {
        init();
    }
    
    public static ListParser getInstance() {
        return singleton_instance;
    }
    
    protected void init() {
        if(this.schoolUrl == null)
            this.schoolUrl = "";
        if(this.menuId == null)
            this.menuId = "";
        if(this.currentPage == null)
            this.currentPage = 1;
        if(this.filterNotice == null)
            this.filterNotice = false;
    }

    public ListParser connect() throws IOException {
        doc = Jsoup.connect(schoolUrl + "/index.jsp")
                .timeout(CONNECT_TIMEOUT * 1000)
                .data("mnu", menuId)
                .data("page", currentPage + "").get();
        return this;
    }
    
    public boolean isFilteringNotice() {
        return this.filterNotice;
    }
    
    public ListParser setFilteringNotice(boolean filterNotice) {
        this.filterNotice = filterNotice;
        
        init();
        
        return this;
    }
    
    public ListParser setCurrentPage(int page) {
        this.currentPage = page;
        
        init();
        
        return this;
    }
    
    public int getCurrentPage() {
        return this.currentPage;
    }
    
    public ListParser setMenuId(String menuId) {
        this.menuId = menuId;
        
        init();
        
        return this;
    }
    
    public String getMenuId() {
        return this.menuId;
    }
    
    public ListParser setSchoolUrl(String schoolUrl) {
        this.schoolUrl = schoolUrl;
        
        init();
        
        return this;
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
            
            Elements indexs = doc.select("thead tr th");
            int titleData_i = 0, writer_i = 0, date_i = 0, visitorCount_i = 0;
            for(int j = 0; j < indexs.size(); j++) {
                if(indexs.get(j).text().equals("제 목"))
                    titleData_i = j;
                else if(indexs.get(j).text().equals("이 름"))
                    writer_i = j;
                else if(indexs.get(j).text().equals("날짜"))
                    date_i = j;
                else if(indexs.get(j).text().equals("조회"))
                    visitorCount_i = j;
                else
                    continue;
            }
            
            Element titleData = article.select("td").get(titleData_i).getElementsByClass("m_ltitle").first();
            boolean isNotice = titleData.select("a span").size() > 0;
            //if filtering notice is true and article is notice
            if((filterNotice && isNotice) || (currentPage > 1 && isNotice))
                continue;
            String date = article.select("td").get(date_i).text();
            String writer = article.select("td").get(writer_i).text();
            int visitorCount = Integer.parseInt(article.select("td").get(visitorCount_i).text(), 10);
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
    
    public String getTitle() throws IOException {
        return MenuTitleParser.getTitle(schoolUrl, menuId);
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