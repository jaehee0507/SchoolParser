package me.blog.colombia2.schoolparser;

import java.lang.*;
import java.net.*;
import java.io.*;
import java.util.*;

import net.htmlparser.jericho.*;

public class Parser {
    public interface onParseFinishListener {
        public void onFinish(ArrayList<String[]> list, ArrayList<ArrayList<String[]>> files);
    }
    
    private static String ORGIN_URL = "http://cw.hs.kr";
    
    private String url;
    private Source source;
    private ArrayList<String[]> list;
    private ArrayList<ArrayList<String[]>> files;
    private onParseFinishListener listener;
    
    public Parser(String url, onParseFinishListener listener) {
        this.url = url;
        this.list = new ArrayList<>();
        this.files = new ArrayList<>();
        this.listener = listener;
    }
    
    public void start() {
        this.list.clear();
        this.files.clear();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL nUrl = new URL(url);
                    InputStream is = nUrl.openStream();
                    source = new Source(new InputStreamReader(is, "EUC-KR"));
                    
                    //From cw.hs.kr's html file
                    Element table = source.getAllElements(HTMLElementName.TABLE).get(0);
                    Element tbody = table.getAllElements(HTMLElementName.TBODY).get(0);
                    Element m_bottom = source.getElementById("m_bottom");
                    Element m_bottom_cp = m_bottom.getAllElements(HTMLElementName.DIV).get(0);
                    Element m_total = m_bottom_cp.getAllElements(HTMLElementName.DL).get(0);
                    String total = m_total.getAllElements(HTMLElementName.DD).get(0).getContent().toString();
                    total = total.substring(0, total.lastIndexOf("건"));
                    int tr_size = Integer.parseInt(total, 10);
                    tr_size = tr_size > 10 ? 10 : tr_size;
                    for(int i = 0; i < tr_size; i++) {
                        Element tr = tbody.getAllElements(HTMLElementName.TR).get(i);
                        Element td = tr.getAllElements(HTMLElementName.TD).get(1);
                        Element div = td.getAllElements(HTMLElementName.DIV).get(0);
                        Element a = div.getAllElements(HTMLElementName.A).get(0);
                        String href = ORGIN_URL+a.getAttributeValue("href");
                        String title = a.getAttributeValue("title");
                        Element td_date = tr.getAllElements(HTMLElementName.TD).get(3);
                        String date = td_date.getContent().toString();
                        int new_art = div.getAllElements(HTMLElementName.IMG).size();
                        Parser.this.list.add(new String[]{title, href, date, new_art > 0 ? "1" : "0"});
                        
                        ArrayList<String[]> attachfiles = new ArrayList<>();
                        Element div_attach = td.getAllElements(HTMLElementName.DIV).get(1);
                        int a_count = div_attach.getAllElements(HTMLElementName.A).size();
                        for(int j = 0; j < a_count; j++) {
                            Element a_attach = div_attach.getAllElements(HTMLElementName.A).get(j);
                            Element img = a_attach.getAllElements(HTMLElementName.IMG).get(0);
                            String alt = img.getAttributeValue("alt");
                            String href_attach = ORGIN_URL+a_attach.getAttributeValue("href");
                            attachfiles.add(new String[]{alt, href_attach});
                        }
                        
                        files.add(attachfiles);
                    }
                    
                    Parser.this.listener.onFinish(Parser.this.list, Parser.this.files);
                } catch(MalformedURLException e) {
                    e.printStackTrace();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
