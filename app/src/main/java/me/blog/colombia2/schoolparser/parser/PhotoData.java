package me.blog.colombia2.schoolparser.parser;

import android.graphics.*;

public class PhotoData {
    protected String title;
    protected Bitmap preview;
    protected String hyperLink;
    
    public PhotoData(String title, Bitmap preview, String hyperLink) {
        this.title = title;
        this.preview = preview;
        this.hyperLink = hyperLink;
    }
    
    public String getTitle() {
        return title;
    }
    
    public Bitmap getPreview() {
        return preview;
    }
    
    public String getHyperLink() {
        return hyperLink;
    }
}
