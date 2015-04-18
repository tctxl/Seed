package com.opdar.framework.db.convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class DateConvert implements Convert<Date> {

    @Override
    public String convert(Object... params) {
        Object date = params[0];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    @Override
    public Date reconvert(Object... params) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(params[0].toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
