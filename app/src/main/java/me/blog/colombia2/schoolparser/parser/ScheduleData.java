package me.blog.colombia2.schoolparser.parser;

public class ScheduleData {
    protected int year;
    protected int month;
    protected int date;
    protected String day;
    protected String data;
    
    public ScheduleData(int year, int month, int date, String day, String data) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.day = day;
        this.data = data;
    }
    
    public String getData() {
        return data;
    }

    public int getDate() {
        return date;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
    
    public String getDay() {
        return day;
    }
}
