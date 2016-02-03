package me.blog.colombia2.schoolparser;

import java.io.*;
import java.net.*;
import java.lang.*;

public class FileDownloader {
    public interface FileDowloadListener {
        public void onDownloadStart(int fileSize);
        public void onDownloading(int currentBytes);
        public void onDownloadError(Exception err);
        public void onDownloadComplete(File result);
    }
    
    protected URL url;
    protected File target;
    protected FileDowloadListener listener;
    
    public FileDownloader(String url, String target) throws MalformedURLException {
        this.url    = new URL(url);
        this.target = new File(target);
    }
    
    public FileDownloader(String url, File target) throws MalformedURLException {
        this.url    = new URL(url);
        this.target = target;
    }
    
    public FileDownloader(URL url, String target) {
        this.url    = url;
        this.target = new File(target);
    }
    
    public FileDownloader(URL url, File target) {
        this.url    = url;
        this.target = target;
    }
    
    public void setFileDownloadListener(FileDowloadListener listener) {
        this.listener = listener;
    }
    
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    target.getParentFile().mkdirs();
                    
                    URLConnection con = url.openConnection();
                    int fileSize = con.getContentLength();
                    BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
                    listener.onDownloadStart(fileSize);
                    
                    byte[] bytes = new byte[4096];
                    int bytes_count = 0;
                    int read = 0;
                    while((read = bis.read(bytes, 0, 4096)) != -1) {
                        bos.write(bytes, 0, read);
                        bytes_count += read;
                        
                        listener.onDownloading(bytes_count);
                    }
                    
                    bis.close();
                    bos.flush();
                    bos.close();
                    listener.onDownloadComplete(target);
                } catch(Exception e) {
                    listener.onDownloadError(e);
                }
            }
        }).start();
    }
}
