package com.opdar.framework.db.interfaces;

import com.opdar.framework.db.impl.FieldModel;
import com.opdar.framework.db.impl.Join;
import com.opdar.framework.db.impl.MappingFilter;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface IDao<T>{
    IDao<T> addMapper(String mapper);
    IDao<T> clearMapper();

    <D>IDao<D> EXTEND(Class<D> clz);

    IDao<T> CloseExtend();

    IDao<T> INSERT(T o);
    IDao<T> JOIN(Join join,String tableName,String synx);
    IDao<T> UPDATE(T o);
    IDao<T> DELETE(T o);

    IDao<T> setFilter(MappingFilter filter);

    IDao<T> SELECT();
    IWhere<T> WHERE(String name, String value);
    IWhere<T> WHERE(IWhere where);
    IDao<T> END();
    List<T> findAll();
    void findEnum(Type enumType);
    T findOne();
    int status();
    Map<String, FieldModel> getFieldNames();
    String getTableName(Class<?> clz);
    String getSimpleTableName(Class<?> clz);

    IDao<T> openTransaction() throws SQLException;

    IDao<T> commit() throws SQLException;

    IDao<T> rollback() throws SQLException;

    void excute(String sql);
    StringBuilder getSqlBuilder();
    void truncateTable();

    void excute(String sql, Class<?> cls);
}
