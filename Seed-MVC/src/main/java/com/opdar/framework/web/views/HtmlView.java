package com.opdar.framework.web.views;

import com.opdar.framework.web.SeedWeb;
import com.opdar.framework.web.interfaces.View;

import java.io.*;

/**
 * Created by 俊帆 on 2015/4/16.
 */
public class HtmlView implements View{
    private String htmlFile;
    private FileView.FileReadListener fileReadListener;

    public HtmlView(String htmlFile){
        this.htmlFile = htmlFile;
    }

    public HtmlView setFileReadListener(FileView.FileReadListener fileReadListener){
        this.fileReadListener = fileReadListener;
        return this;
    }

    public byte[] renderView() {
        InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream(SeedWeb.WEB_HTML_PATH.concat("/").concat(htmlFile));
        return new FileView(file,fileReadListener).renderView();
    }

    public String contentType() {
        return "text/html";
    }

    public int getCode() {
        return 200;
    }
}
