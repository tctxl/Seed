package com.opdar.framework.web.views;

import com.opdar.framework.web.common.HttpResponseCode;
import com.opdar.framework.web.interfaces.View;

import java.io.*;

/**
 * Created by 俊帆 on 2015/4/16.
 */
public class FileView implements View{
    private  String contentType = "text/html";
    private InputStream file;
    public FileView(String file){
        this(new File(file));
    }
    public FileView(InputStream file){
        this(file,"text/html");
    }
    public FileView(InputStream file,String contentType){
        this.file = file;
        this.contentType = contentType;
    }

    public FileView(File file){
        try {
            this.file = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public FileView(File file,String contentType){
        try {
            this.file = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.contentType = contentType;
    }

    public byte[] renderView() {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[file.available()];
            file.read(buffer);
            out.write(buffer);
            return buffer;
        } catch (Exception e) {
        } finally {
            try {
                if (file != null)
                    file.close();
                if(out != null)out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }
        return new ErrorView(HttpResponseCode.CODE_404).renderView();
    }

    public String contentType() {
        return contentType;
    }

    public int getCode() {
        return 200;
    }
}
