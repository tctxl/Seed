package com.opdar.framework.db.impl;


import com.opdar.framework.db.convert.*;
import com.opdar.framework.db.interfaces.IDao;
import com.opdar.framework.db.interfaces.IDatabase;
import com.opdar.framework.utils.ThreadLocalUtils;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class BaseDatabase implements IDatabase {
    private DataSource dataSource;
    private HashMap<Class<?>, Convert<?>> converts = new LinkedHashMap<Class<?>, Convert<?>>();
    private OnDataSourceCloseListener onDataSourceCloseListener;
    public BaseDatabase(DataSource dataSource, OnDataSourceCloseListener onDataSourceCloseListener) {
        this.dataSource = dataSource;
        this.onDataSourceCloseListener = onDataSourceCloseListener;
        converts.put(Date.class, new DateConvert());
        converts.put(Timestamp.class, new TimestampConvert());
        converts.put(Integer.class, new IntegerConvert());
        converts.put(Byte.class, new ByteConvert());
        converts.put(Long.class, new LongConvert());
        converts.put(Character.class, new CharacterConvert());
        converts.put(Enum.class, new EnumConvert());
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> IDao<T> getDao(Class<T> cls) {
        IDao<T> dao = null;
        dao = DaoMap.get(cls);
        if (dao == null)
            DaoMap.put(cls, dao = new BaseDaoImpl<T>(dataSource, cls, this));
        return dao;
    }

    @Override
    public void addConvert(Class<?> clz, Convert convert) {
        converts.put(clz, convert);
    }

    public HashMap<Class<?>, Convert<?>> getConverts() {
        return converts;
    }

    public void setConverts(HashMap<Class<?>, Convert<?>> converts) {
        this.converts = converts;
    }

    public void close(){
        if(onDataSourceCloseListener != null)onDataSourceCloseListener.close(dataSource);
    }
}
