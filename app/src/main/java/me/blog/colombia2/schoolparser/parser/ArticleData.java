package me.blog.colombia2.schoolparser.parser;

import java.util.*;

public class ArticleData {
    final protected String title;
    final protected String date;
    final protected String writer;
    final protected String hyperLink;
    final protected int visitorCount;
    final protected boolean isNotice;
    final protected boolean hasReply;
    final protected boolean isNew;
    final protected ArrayList<FileData> attachments;

    public ArticleData(String title, String date, String writer, String hyperLink, int visitorCount, boolean isNotice, boolean hasReply, boolean isNew, ArrayList<FileData> attachments) {
        this.title = title;
        this.date = date;
        this.writer = writer;
        this.hyperLink = hyperLink;
        this.visitorCount = visitorCount;
        this.isNotice = isNotice;
        this.hasReply = hasReply;
        this.isNew = isNew;
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
    
    public boolean hasReply() {
        return this.hasReply;
    }
    
    public boolean isNew() {
        return this.isNew;
    }

    public ArrayList<FileData> getAttachments() {
        return this.attachments;
    }
}
