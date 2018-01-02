package me.blog.colombia2.schoolparser.sql;

/**
 * Created by Administrator on 2017-03-25.
 */

public class SQLReply {
    protected int order;
    protected String name;
    protected String pass;
    protected String content;
    protected String wdate;
    protected String ip;

    public int getOrder() {
        return order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWdate() {
        return wdate;
    }

    public void setWdate(String wdate) {
        this.wdate = wdate;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
