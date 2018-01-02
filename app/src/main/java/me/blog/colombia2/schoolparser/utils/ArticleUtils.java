package me.blog.colombia2.schoolparser.utils;

import java.net.*;
import org.jsoup.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ArticleUtils {
    public static int getSubMenuCount(String url) throws IOException {
        int i = 0;
        while(true) {
            if(Jsoup.connect(url+String.format("%03d", i+1)).get().select("title").first().text().equals("청원고등학교 홈페이지입니다."))
                break;
            else
                i++;
        }
        return i;
    }

    public static String sha1(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
}
