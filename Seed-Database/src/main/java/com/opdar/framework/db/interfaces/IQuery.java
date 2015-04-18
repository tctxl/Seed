package com.opdar.framework.db.interfaces;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface IQuery extends IDao {
    public IQuery INNER_JOIN();
    public IQuery LEFT_JOIN();
    public IQuery RIGHT_JOIN();
    public IQuery FULL_JOIN();
    IQuery setDao(IDao dao);
}
