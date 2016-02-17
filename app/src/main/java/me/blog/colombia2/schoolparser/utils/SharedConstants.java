package me.blog.colombia2.schoolparser.utils;

import java.util.*;
import me.blog.colombia2.schoolparser.parser.*;

public class SharedConstants {
    public ArrayList<FileData> ATTACHMENTS;
    public String SCHOOL_URL;
    public ArrayList<String> MENUS;
    public ArrayList<String> MENU_NAMES;
    
    private static SharedConstants instance = new SharedConstants();
    
    public static SharedConstants getInstance() {
        return instance;
    }
    
    private SharedConstants() {
        SCHOOL_URL = "http://cw.hs.kr";
        MENUS = new ArrayList<>();
        MENU_NAMES = new ArrayList<>();
    }
}
