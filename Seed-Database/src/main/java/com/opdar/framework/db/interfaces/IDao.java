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
    /**
     * 额外的字段映射至对象
     * @param mapper
     * @return
     */
    IDao<T> addMapper(String mapper);

    /**
     * 清除映射
     * @return
     */
    IDao<T> clearMapper();

    /**
     * connection继承
     * @param clz
     * @param <D>
     * @return
     */
    <D>IDao<D> extend(Class<D> clz);

    /**
     * 关闭connection继承
     * @return
     */
    IDao<T> closeExtend();

    /**
     * 插入对象
     * @param o
     * @return
     */
    IDao<T> insert(T o);

    /**
     * JOIN关联操作
     * @param join
     * @param tableName
     * @param synx
     * @return
     */
    IDao<T> join(Join join,String tableName,String synx);

    /**
     * 更新对象
     * @param o
     * @return
     */
    IDao<T> update(T o);

    /**
     * 删除语句
     * @return
     */
    IDao<T> delete();

    /**
     * 过滤器，被过滤字段不查询
     * @param filter
     * @return
     */
    IDao<T> setFilter(MappingFilter filter);

    /**
     * 开始查询语句
     * @return
     */
    IDao<T> select();

    /**
     * count语句
     * @return
     */
    IDao<T> count();

    /**
     * where条件
     * @param name
     * @param value
     * @return
     */
    IWhere<T> where(String name, String value);

    /**
     * where条件
     * @param where
     * @return
     */
    IWhere<T> where(IWhere where);

    /**
     * 结束语句
     * @return
     */
    IDao<T> end();

    /**
     * 结束语句
     * @return
     */
    IDao<T> end(Class<?> cls);

    /**
     * 找到所有返回条目
     * @return
     */
    <K>List<K> findAll();

    /**
     * 获得枚举类型
     * @param enumType
     */
    void findEnum(Type enumType);

    /**
     * 获得一个对象
     * @return
     */
    <K>K findOne();

    /**
     * C/U状态
     * @return
     */
    int status();

    /**
     *
     * @return
     */
    Map<String, FieldModel> getFieldNames();

    /**
     * 得到表名
     * @param clz
     * @return
     */
    String getTableName(Class<?> clz);

    /**
     * 得到表简拼
     * @param clz
     * @return
     */
    String getSimpleTableName(Class<?> clz);

    /**
     * 打开事务
     * @return
     * @throws SQLException
     */
    IDao<T> openTransaction() throws SQLException;

    /**
     * 提交
     * @return
     * @throws SQLException
     */
    IDao<T> commit() throws SQLException;

    /**
     * 回滚
     * @return
     * @throws SQLException
     */
    IDao<T> rollback() throws SQLException;

    /**
     * 执行语句
     * @param sql 语句
     */
    void excute(String sql);

    /**
     * sql语句
     * @return
     */
    StringBuilder getSqlBuilder();

    /**
     * 清空表
     */
    void truncateTable();

    /**
     * 执行语句
     * @param sql 语句
     * @param cls 返回类型
     */
    void excute(String sql, Class<?> cls);
}
