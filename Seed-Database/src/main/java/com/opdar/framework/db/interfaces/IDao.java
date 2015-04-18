package com.opdar.framework.db.interfaces;

import com.opdar.framework.db.impl.Join;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface IDao<T>{
    IDao<T> addMapper(String mapper);

    IDao<T> clearMapper();

    public IDao<T> INSERT(Object o);

    void JOIN(Join join,String tableName,String synx);

    public IDao<T> UPDATE(Object o);
    public IDao<T> DELETE(Object o);
    public IDao<T> SELECT(Class<T> clz);
    public IWhere<T> WHERE(String name, String value);
    public IWhere<T> WHERE(IWhere where);
    public IDao<T> END();
    public List<T> findAll();
    public void findEnum(Type enumType);
    public T findOne();
    public int status();
    public String getTableName(Class<?> clz);

    String getSimpleTableName(Class<?> clz);

    public void excute(String sql);
    public StringBuilder getSqlBuilder();

    public void truncateTable();

}
