package me.blog.colombia2.schoolparser.parser;

import android.graphics.*;

public class PhotoData {
    protected String title;
    protected Bitmap preview;
    
    public PhotoData(String title, Bitmap preview) {
        this.title = title;
        this.preview = preview;
    }
    
    public String getTitle() {
        return title;
    }
    
    public Bitmap getPreview() {
        return preview;
    }
}
