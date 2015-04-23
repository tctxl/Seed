package com.opdar.framework.db.interfaces;

import com.opdar.framework.db.impl.Join;

import java.lang.reflect.Type;
import java.sql.SQLException;
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
    IDao<T> INSERT(T o);
    void JOIN(Join join,String tableName,String synx);
    IDao<T> UPDATE(Object o);
    IDao<T> DELETE(Object o);
    IDao<T> SELECT();
    IWhere<T> WHERE(String name, String value);
    IWhere<T> WHERE(IWhere where);
    IDao<T> END();
    List<T> findAll();
    void findEnum(Type enumType);
    T findOne();
    int status();
    String getTableName(Class<?> clz);
    String getSimpleTableName(Class<?> clz);

    IDao<T> openTransaction() throws SQLException;

    IDao<T> commit() throws SQLException;

    void excute(String sql);
    StringBuilder getSqlBuilder();
    void truncateTable();
}
