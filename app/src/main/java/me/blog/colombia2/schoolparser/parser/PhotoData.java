package me.blog.colombia2.schoolparser.parser;

import android.graphics.*;
import java.net.*;

public class PhotoData {
    protected String title;
    protected String previewUrl;
    protected String hyperLink;
    
    public PhotoData(String title, String previewUrl, String hyperLink) {
        this.title = title;
        this.previewUrl = previewUrl;
        this.hyperLink = hyperLink;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getPreview() {
        return previewUrl;
    }
    
    public String getHyperLink() {
        return hyperLink;
    }
}
