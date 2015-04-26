package com.opdar.framework.db.impl;


import com.opdar.framework.db.interfaces.IDao;
import com.opdar.framework.db.interfaces.IWhere;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class BaseWhere<T> implements IWhere<T> {

    private StringBuilder whereBuilder = new StringBuilder();
    private IDao dao;

    @Override
    public IWhere LEFT_BRACKET() {
        whereBuilder.append(" ( ");
        return this;
    }

    @Override
    public IWhere RIGHT_BRACKET() {
        whereBuilder.append(" ) ");
        return this;
    }

    @Override
    public IWhere IS(String name, String value) {
        whereBuilder.append(name).append("=");
        if(value.indexOf("${")==0&&value.lastIndexOf("}")==value.length()-1){
            whereBuilder.append(value.substring(2,value.length()-1));
        }else{
            whereBuilder.append("'").append(value).append("'");
        }
        return this;
    }

    @Override
    public IWhere NOT_IS(String name, String value) {
        whereBuilder.append(name).append("<>");
        if(value.indexOf("${")==0&&value.lastIndexOf("}")==value.length()-1){
            whereBuilder.append(value.substring(2,value.length()-1));
        }else{
            whereBuilder.append("'").append(value).append("'");
        }
        return this;
    }

    @Override
    public IWhere GT(String name, String value) {
        whereBuilder.append(name).append(">");
        if(value.indexOf("${")==0&&value.lastIndexOf("}")==value.length()-1){
            whereBuilder.append(value.substring(2,value.length()-1));
        }else{
            whereBuilder.append("'").append(value).append("'");
        }
        return this;
    }

    @Override
    public IWhere GTIS(String name, String value) {
        whereBuilder.append(name).append(">=");

        if(value.indexOf("${")==0&&value.lastIndexOf("}")==value.length()-1){
            whereBuilder.append(value.substring(2,value.length()-1));
        }else{
            whereBuilder.append("'").append(value).append("'");
        }
        return this;
    }

    @Override
    public IWhere LT(String name, String value) {
        whereBuilder.append(name).append("<");

        if(value.indexOf("${")==0&&value.lastIndexOf("}")==value.length()-1){
            whereBuilder.append(value.substring(2,value.length()-1));
        }else{
            whereBuilder.append("'").append(value).append("'");
        }
        return this;
    }

    @Override
    public IWhere LTIS(String name, String value) {
        whereBuilder.append(name).append("<=");

        if(value.indexOf("${")==0&&value.lastIndexOf("}")==value.length()-1){
            whereBuilder.append(value.substring(2,value.length()-1));
        }else{
            whereBuilder.append("'").append(value).append("'");
        }
        return this;
    }

    @Override
    public IWhere LIKE(String name, String value) {
        whereBuilder.append(name).append(" like ").append("'").append(value).append("'");
        return this;
    }

    @Override
    public IWhere AND() {
        whereBuilder.append(" AND ");
        return this;
    }

    @Override
    public IWhere OR() {
        whereBuilder.append(" OR ");
        return this;
    }

    @Override
    public IWhere<T> IN(String fieldName,Object...params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for(Object o:params){
            stringBuilder.append("'").append(String.valueOf(o)).append("',");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        stringBuilder.append(")");
        whereBuilder.append(fieldName).append(" IN ").append(stringBuilder);
        return this;
    }

    @Override
    public IWhere<T> IN(String fieldName,String params) {
        whereBuilder.append(fieldName).append(" IN ").append("(").append(params).append(")");
        return this;
    }

    @Override
    public IWhere LIMIT(int start, int limit) {
        whereBuilder.append(" limit " + start + "," + limit);
        return this;
    }

    @Override
    public IWhere<T> GROUPBY(String... params) {
        whereBuilder.append(" group by ");
        for (int i = 0; i < params.length; i++) {
            whereBuilder.append(params[i]);
            if (i != params.length - 1) {
                whereBuilder.append(",");
            }

        }
        return this;
    }

    public static class Order {
        public enum OrderType {
            ASC, DESC
        }

        private String name;
        private OrderType type = OrderType.DESC;

        public Order(String name, OrderType type) {
            this.name = name;
            this.type = type;
        }
    }

    @Override
    public IWhere<T> ORDERBY(Order... params) {
        whereBuilder.append(" order by ");
        for (int i = 0; i < params.length; i++) {
            whereBuilder.append(params[i].name).append(" ").append(params[i].type.name());
            if (i != params.length - 1) {
                whereBuilder.append(",");
            }

        }
        return this;
    }

    @Override
    public IDao<T> WhereEND() {
        return dao;
    }

    @Override
    public void setDao(IDao<T> dao) {
        this.dao = dao;
    }

    @Override
    public String toString() {
        return whereBuilder.toString();
    }
}
