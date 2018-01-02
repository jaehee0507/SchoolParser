package me.blog.colombia2.schoolparser.sql;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import me.blog.colombia2.schoolparser.utils.ArticleUtils;

/**
 * Created by Administrator on 2017-03-18.
 */

public class ArticleSQLManager {
    protected int currentPage = 1;

    public ArticleSQLManager() {

    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalArticles() {
        int result = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new URL("http://cheongwongo1004.esy.es/getdata.php?start=-1").openStream()));
            String read = br.readLine();
            br.close();
            result = Integer.parseInt(read, 10);
        } catch(Exception e) {
            Log.e("cheong", e.toString());
        }
        return result;
    }

    public int getMaxPage() {
        return (int) Math.ceil((double) getTotalArticles() / 10.0);
    }

    public ArrayList<SQLArticle> getArticles() {
        try {
            ArrayList<SQLArticle> result = new ArrayList<>();
            URL url = new URL("http://cheongwongo1004.esy.es/getdata.php?start="+(getCurrentPage()-1)*10);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuilder builder = new StringBuilder();
            String json;
            while((json = br.readLine())!= null){
                builder.append(json+"\n");
            }

            String jsonStr = builder.toString().trim();
            br.close();

            JSONObject obj = (JSONObject) new JSONParser().parse(jsonStr);
            JSONArray arr = (JSONArray) obj.get("result");
            for(int i=0; i < arr.size(); i++) {
                JSONObject article = (JSONObject) arr.get(i);
                if(article.get("id").toString().equals("-1"))
                    continue;
                SQLArticle articleObj = new SQLArticle();
                articleObj.setId(Integer.parseInt(article.get("id")+"", 10));
                articleObj.setContent(article.get("content")+"");
                articleObj.setIp(article.get("ip")+"");
                articleObj.setName(article.get("name")+"");
                articleObj.setPass(article.get("pass")+"");
                articleObj.setTitle(article.get("title")+"");
                articleObj.setWdate(article.get("wdate")+"");
                articleObj.setView(Integer.parseInt(article.get("view")+"", 10));
                articleObj.setReplyCount(Integer.parseInt(article.get("reply_count")+"", 10));
                result.add(articleObj);
            }

            return result;
        } catch(Exception e) {
            Log.e("cheong", e.toString());
        }

        return null;
    }

    public static boolean writeArticle(String name, String pass, String title, String content) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("http://cheongwongo1004.esy.es/insert.php?");
            builder.append("name=" + URLEncoder.encode(name, "UTF-8")).append("&");
            builder.append("title=" + URLEncoder.encode(title, "UTF-8")).append("&");
            builder.append("pass=" + ArticleUtils.sha1(pass)).append("&");
            Date date = new Date();
            DateFormat wdate = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
            wdate.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            builder.append("wdate=" + wdate.format(date));

            URL url = new URL(builder.toString());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream());
            PrintWriter writer = new PrintWriter(outStream);
            writer.write("content="+content);
            writer.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String read = br.readLine();
            br.close();
            return read.equals("success");
        } catch(Exception e) {
            Log.e("cheong", e.toString());
        }

        return false;
    }

    public static boolean updateArticle(int id, String title, String content, int view) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("http://cheongwongo1004.esy.es/update.php?");
            builder.append("id=" + id).append("&");
            builder.append("view=" + view).append("&");
            builder.append("title=" + URLEncoder.encode(title, "UTF-8"));

            URL url = new URL(builder.toString());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream());
            PrintWriter writer = new PrintWriter(outStream);
            writer.write("content="+content);
            writer.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String read = br.readLine();
            br.close();
            return read.equals("success");
        } catch(Exception e) {
            Log.e("cheong", e.toString());
        }

        return false;
    }

    public static boolean deleteArticle(int id) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("http://cheongwongo1004.esy.es/delete.php?");
            builder.append("id=" + id);

            BufferedReader br = new BufferedReader(new InputStreamReader(new URL(builder.toString()).openStream()));
            String read = br.readLine();
            br.close();
            return read.equals("success");
        } catch(Exception e) {
            Log.e("cheong", e.toString());
        }

        return false;
    }

    public static boolean writeComment(int article_id, String name, String pass, String content) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("http://cheongwongo1004.esy.es/writecomment.php?");
            builder.append("article_id=" + article_id).append("&");
            builder.append("name=" + URLEncoder.encode(name, "UTF-8")).append("&");
            builder.append("pass=" + ArticleUtils.sha1(pass)).append("&");
            Date date = new Date();
            DateFormat wdate = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
            wdate.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            builder.append("wdate=" + wdate.format(date));

            URL url = new URL(builder.toString());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream());
            PrintWriter writer = new PrintWriter(outStream);
            writer.write("content="+content);
            writer.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String read = br.readLine();
            br.close();
            return read.equals("success");
        } catch(Exception e) {
            Log.e("cheong", e.toString());
        }

        return false;
    }

    public static ArrayList<SQLReply> getReplies(int article_id) {
        try {
            ArrayList<SQLReply> result = new ArrayList<>();
            URL url = new URL("http://cheongwongo1004.esy.es/getcomment.php?article_id="+article_id);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuilder builder = new StringBuilder();
            String json;
            while((json = br.readLine())!= null){
                builder.append(json+"\n");
            }

            String jsonStr = builder.toString().trim();
            br.close();

            JSONObject obj = (JSONObject) new JSONParser().parse(jsonStr);
            JSONArray arr = (JSONArray) obj.get("result");
            for(int i=0; i < arr.size(); i++) {
                JSONObject reply = (JSONObject) arr.get(i);
                SQLReply replyObj = new SQLReply();
                replyObj.setOrder(Integer.parseInt(reply.get("co_order")+"", 10));
                replyObj.setContent(reply.get("content")+"");
                replyObj.setIp(reply.get("ip")+"");
                replyObj.setName(reply.get("name")+"");
                replyObj.setPass(reply.get("pass")+"");
                replyObj.setWdate(reply.get("wdate")+"");
                result.add(replyObj);
            }

            return result;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean deleteComment(int article_id, int co_order) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("http://cheongwongo1004.esy.es/deletecomment.php?");
            builder.append("article_id=" + article_id).append("&");
            builder.append("co_order=" + co_order);

            BufferedReader br = new BufferedReader(new InputStreamReader(new URL(builder.toString()).openStream()));
            String read = br.readLine();
            br.close();
            return read.equals("success");
        } catch(Exception e) {
            Log.e("cheong", e.toString());
        }

        return false;
    }
}
