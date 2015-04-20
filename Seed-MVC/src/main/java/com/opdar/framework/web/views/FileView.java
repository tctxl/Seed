package com.opdar.framework.web.views;

import com.opdar.framework.web.common.HttpResponseCode;
import com.opdar.framework.web.interfaces.View;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by 俊帆 on 2015/4/16.
 */
public class FileView implements View{
    private FileReadListener fileReadListener;
    private  String contentType = "text/html";
    private InputStream file;
    public FileView(String file){
        this(new File(file));
    }

    public FileView(InputStream file,FileReadListener fileReadListener){
        this(file,"text/html",fileReadListener);
    }
    public interface FileReadListener{
        void read(byte[] bytes,String contentType,int responseCode);
        void catchException(Throwable throwable);
        void close();
    }

    public FileView(InputStream file,String contentType,FileReadListener fileReadListener){
        this.file = file;
        this.contentType = contentType;
        this.fileReadListener = fileReadListener;
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
        try {
            byte[] buffer = new byte[file.available()];
            if(fileReadListener != null){
                fileReadListener.read(buffer, contentType(), getCode());
            }
        } catch (Exception e) {
            if(fileReadListener!=null)
                fileReadListener.catchException(e);
        } finally {
            try {
                if (file != null)
                    file.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            if(fileReadListener!=null)
                fileReadListener.close();
        }
        return null;
    }

    public String contentType() {
        return contentType;
    }

    public int getCode() {
        return 200;
    }
}
