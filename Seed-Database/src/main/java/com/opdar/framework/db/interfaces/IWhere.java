package com.opdar.framework.db.interfaces;


import com.opdar.framework.db.impl.BaseWhere;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface IWhere<T> {
    IWhere LEFT_BRACKET();

    IWhere RIGHT_BRACKET();

    public IWhere<T> IS(String name,String value);
    public IWhere<T> NOT_IS(String name,String value);
    public IWhere<T> GT(String name,String value);
    public IWhere<T> GTIS(String name,String value);
    public IWhere<T> LT(String name,String value);
    public IWhere<T> LTIS(String name,String value);
    public IWhere<T> LIKE(String name,String value);
    public IWhere<T> AND();
    public IWhere<T> OR();
    public IWhere<T> IN(String fieldName,Object...params);

    IWhere<T> IN(String fieldName,String params);

    public IWhere<T> LIMIT(int start,int limit);
    public IWhere<T> GROUPBY(String...p);

    IWhere<T> ORDERBY(BaseWhere.Order... params);

    public IDao<T> WhereEND();

    void setDao(IDao<T> dao);
}
