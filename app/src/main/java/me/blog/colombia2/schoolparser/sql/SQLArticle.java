package me.blog.colombia2.schoolparser.sql;

/**
 * Created by Administrator on 2017-03-18.
 */

public class SQLArticle {
    protected int id;
    protected String name;
    protected String pass;
    protected String title;
    protected String content;
    protected String wdate;
    protected String ip;
    protected int view;
    protected int replyCount;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getIp() {
        return ip;
    }

    public String getWdate() {
        return wdate;
    }

    public int getView() {
        return view;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setView(int view) {
        this.view = view;
    }

    public void setWdate(String wdate) {
        this.wdate = wdate;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }
}
