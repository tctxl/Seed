package com.opdar.framework.web.views;

import com.opdar.framework.utils.Utils;
import com.opdar.framework.web.common.HttpResponseCode;
import com.opdar.framework.web.interfaces.View;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/4/16.
 */
public class FileView implements View{
    private FileReadListener fileReadListener;
    private  String contentType = "text/html";
    private byte[] file;
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
        try {
            this.file = Utils.is2byte(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.contentType = contentType;
        this.fileReadListener = fileReadListener;
    }

    public FileView(File file){
        this(file,"text/html");
    }

    public FileView(File file,String contentType){
        this(file,contentType,null);
    }

    public FileView(File file,String contentType,FileReadListener fileReadListener){
        byte[] buffer = new byte[(int) file.length()];
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file,"r");
            raf.read(buffer);
            this.file = buffer;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }finally {
            try {
                if(raf != null)
                    raf.close();
            } catch (IOException e) {
            }
        }
        this.contentType = contentType;
        this.fileReadListener = fileReadListener;
    }

    @Override
    public Map<String, String> headers() {
        return null;
    }

    public byte[] renderView() {
        try {
            if(fileReadListener != null){
                fileReadListener.read(file, contentType(), getCode());
            }
        } catch (Exception e) {
            if(fileReadListener!=null)
                fileReadListener.catchException(e);
        } finally {
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
