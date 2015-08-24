package com.opdar.seed.extra.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/8/22.
 */
public class NetUtils {

    public static class NetResult{
        private Decoder decoder;
        private byte[] result;
        private int code;

        public NetResult(Decoder decoder) {
            this.decoder = decoder;
        }

        public byte[] getResult() {
            return result;
        }
        public String getResultStr() {
            try {
                return new String(result,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "";
        }


        public void setResult(byte[] result) {
            this.result = decoder.decoder(result);
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public synchronized static NetResult post(String reqUrl, Map<String, Object> parameters, Map<String, Object> headers, String body,Decoder decoder) throws Exception {
        HttpURLConnection urlConn = null;
        NetResult responseContent = new NetResult(decoder);
        try {
            StringBuffer params = new StringBuffer();
            for (Iterator iter = parameters.keySet().iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                Object value = parameters.get(key);
                if (value == null) {
                    continue;
                }
                if (value.getClass().isArray()) {
                    Object[] objs = (Object[]) value;
                    for (Object o : objs) {
                        params.append(key.toString());
                        params.append("=");
                        params.append(URLEncoder.encode(o.toString(), "UTF-8"));
                        params.append("&");
                    }
                } else {
                    params.append(key.toString());
                    params.append("=");
                    params.append(URLEncoder.encode(value.toString(), "UTF-8"));
                    params.append("&");
                }
            }
            URL url = new URL(reqUrl);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setConnectTimeout(5000);
            urlConn.setReadTimeout(5000);
            urlConn.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            if(headers != null){
                for(Iterator<String> it = headers.keySet().iterator();it.hasNext();){
                    String key = it.next();
                    urlConn.setRequestProperty(key,headers.get(key).toString());
                }
            }
            if(StringUtils.isBlank(body)){
                urlConn.getOutputStream().write(b, 0, b.length);
            }else{
                byte[] b2 = body.toString().getBytes();
                urlConn.getOutputStream().write(b2, 0, b2.length);
            }
            urlConn.getOutputStream().flush();
            urlConn.getOutputStream().close();
            InputStream in = null;
            try{
                in = urlConn.getInputStream();
                byte[] bytes = read(in);
                responseContent.setResult(bytes);
            }catch (FileNotFoundException e){

            }finally {
                if(in != null) in.close();
            }
            responseContent.setCode(urlConn.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
        return responseContent;
    }

    public static byte[] read(InputStream inputStream) throws IOException {
        ByteArrayOutputStream arrayBuffer = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(b)) != -1) {
            arrayBuffer.write(b, 0, len);
        }
        inputStream.close();
        arrayBuffer.close();
        return arrayBuffer.toByteArray();
    }
}
