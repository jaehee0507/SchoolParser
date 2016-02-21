package me.blog.colombia2.schoolparser.parser;

import android.text.*;

public class ReplyData {
    final protected String nickname;
    final protected Spanned content;
    
    public ReplyData(String nickname, Spanned content) {
        this.nickname = nickname;
        this.content = content;
    }
    
    public String getNickname() {
        return this.nickname;
    }
    
    public Spanned getContent() {
        return this.content;
    }
}
