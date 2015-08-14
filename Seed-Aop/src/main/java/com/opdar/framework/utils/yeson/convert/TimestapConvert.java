package com.opdar.framework.utils.yeson.convert;

import java.sql.Timestamp;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class TimestapConvert implements JSONConvert<Timestamp> {

    @Override
    public Object convert(Timestamp o) {
        if(o == null)return "\"\"";
        return o.getTime();
    }

}
