package com.opdar.framework.db.impl;

import com.opdar.framework.db.anotations.Field;
import com.opdar.framework.db.anotations.NoSelect;
import com.opdar.framework.db.anotations.Table;
import com.opdar.framework.db.convert.Convert;
import com.opdar.framework.db.interfaces.EnumValue;
import com.opdar.framework.db.interfaces.IDao;
import com.opdar.framework.db.interfaces.IWhere;
import com.opdar.framework.utils.FieldModel;
import com.opdar.framework.utils.ParamsUtil;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class BaseDaoImpl<T> implements IDao<T> {
    private final BaseDatabase baseDatabase;
    private FieldRule fr = FieldRule.UPPER_R_UNDERLINE;
    private String prefix = "t_";

    public enum FieldRule {
        UPPER_R_UNDERLINE
    }

    private int lastUpdateCount = 0;
    private DataSource dataSource;
    private StringBuilder sqlBuilder;
    private Set<IWhere> wheres = new HashSet<IWhere>();
    private ArrayList<T> values = new ArrayList<T>();
    private ArrayList<String> mapper = new ArrayList<String>();
    private Class<T> cls;

    public BaseDaoImpl(DataSource dataSource, Class<T> cls, BaseDatabase baseDatabase) {
        this.dataSource = dataSource;
        this.sqlBuilder = new StringBuilder();
        this.cls = cls;
        this.baseDatabase = baseDatabase;
    }

    public String replaceFieldName(String name) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            switch (fr) {
                case UPPER_R_UNDERLINE:
                    if (Character.isUpperCase(c)) {
                        stringBuilder.append("_");
                    }
                    break;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    @Override
    public IDao<T> addMapper(String _mapper){
        this.mapper.add(_mapper);
        return this;
    }

    @Override
    public IDao<T> clearMapper(){
        this.mapper.clear();
        return this;
    }

    private void clear() {
        lastUpdateCount = 0;
        wheres.clear();
        values.clear();
        sqlBuilder.delete(0, sqlBuilder.length());
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private String getValue(Class type, Object value) {
        if (value == null) return null;
        if (baseDatabase.getConverts().containsKey(type)) {
            Convert convert = baseDatabase.getConverts().get(type);
            return convert.convert(value);
        }
        return String.valueOf(value);
    }

    @Override
    public IDao<T> INSERT(Object o) {
        clear();
        Set<FieldModel> values = ParamsUtil.getField(o);
        String tableName = getTableName(o.getClass());
        if (tableName != null) {
            StringBuilder valueBuilder = new StringBuilder();
            sqlBuilder.append("insert into ");
            sqlBuilder.append(tableName);
            sqlBuilder.append(" (");
            for (FieldModel fieldModel : values) {
                Field field = fieldModel.getField().getAnnotation(Field.class);
                if (field != null && field.ignore()) continue;
                if (fieldModel.getName().equals("serialVersionUID")) continue;
                sqlBuilder.append(replaceFieldName(fieldModel.getName()));
                sqlBuilder.append(",");
                if (!(fieldModel.getType().getSort() > 1 && fieldModel.getType().getSort() < 9))
                    valueBuilder.append("'");
                valueBuilder.append(getValue(fieldModel.getField().getType(), fieldModel.getValue()));
                if (!(fieldModel.getType().getSort() > 1 && fieldModel.getType().getSort() < 9))
                    valueBuilder.append("'");
                valueBuilder.append(",");
            }
            sqlBuilder.delete(sqlBuilder.length() - 1, sqlBuilder.length());
            valueBuilder.delete(valueBuilder.length() - 1, valueBuilder.length());
            sqlBuilder.append(") values (");
            sqlBuilder.append(valueBuilder);
            sqlBuilder.append(")");
        }
        return this;
    }

    public StringBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    @Override
    public void truncateTable() {
        excute("TRUNCATE TABLE " + getTableName(cls));
    }

    @Override
    public void JOIN(Join join, String tableName, String synx) {
        sqlBuilder.append(" ").append(join.name()).append(" JOIN ").append(tableName).append(" ON ").append(synx);
    }

    @Override
    public IDao<T> UPDATE(Object o) {
        clear();
        Class<?> clz = o.getClass();
        Set<FieldModel> values = ParamsUtil.getField(o);
        String tableName = getTableName(clz);
        if (tableName != null) {
//            UPDATE T_Message SET MSG_READ =1 WHERE TID = #{value}
            sqlBuilder.append("update ").append(tableName).append(" set ");
            for (FieldModel fieldModel : values) {
                if (fieldModel.getName().equals("serialVersionUID")) continue;
                int sort = fieldModel.getType().getSort();
                if (fieldModel.getValue() != null) {
                    if ((sort > 0 && sort < 9) || sort == 10) {
                        String value = String.valueOf(getValue(fieldModel.getField().getType(), fieldModel.getValue()));
                        sqlBuilder.append(replaceFieldName(fieldModel.getName())).append("=").append("'").append(value).append("'");
                    }
                }
                sqlBuilder.append(",");
            }
            sqlBuilder.delete(sqlBuilder.length() - 1, sqlBuilder.length());
        }
        return this;
    }

    @Override
    public IDao<T> DELETE(Object o) {
        clear();
        return this;
    }

    @Override
    public IDao SELECT(Class<T> clz) {
        clear();
//        SELECT * FROM 表名�?
        String tableName = getTableName(clz);
        if (tableName != null) {
            sqlBuilder.append("select  ");
            for(String map:mapper){
                sqlBuilder.append(map).append(" ,");
            }
            String simpleTableName = getSimpleTableName(clz)+".";
            if (EnumValue.class.isAssignableFrom(clz)) {
                String baseName = clz.getSimpleName().toLowerCase();
                String enumName = replaceFieldName(baseName.concat("Name"));
                String enumValue = replaceFieldName(baseName.concat("Value "));
                sqlBuilder.append(simpleTableName+enumName).append(",").append(simpleTableName+enumValue);
            } else {
                Set<FieldModel> values = ParamsUtil.getField(clz);
                for (FieldModel fieldModel : values) {
                    if (fieldModel.getName().equals("serialVersionUID")) continue;
                    Field field = fieldModel.getField().getAnnotation(Field.class);
                    NoSelect noSelect = fieldModel.getField().getAnnotation(NoSelect.class);
                    if (field != null && field.ignore()) {
                        continue;
                    }
                    if(noSelect!=null && noSelect.value())continue;

                    sqlBuilder.append(simpleTableName + replaceFieldName(fieldModel.getName()));
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.delete(sqlBuilder.length() - 1, sqlBuilder.length());
            sqlBuilder.append(" from ");
            sqlBuilder.append(tableName).append(" ").append(getSimpleTableName(clz));
        }
        return this;
    }

    @Override
    public IWhere<T> WHERE(String name, String value) {
        BaseWhere baseWhere = new BaseWhere();
        baseWhere.IS(name, value);
        return WHERE(baseWhere);
    }

    @Override
    public IWhere<T> WHERE(IWhere where) {
        where.setDao(this);
        wheres.add(where);
        return where;
    }

    @Override
    public IDao<T> END() {
        if (wheres.size() > 0) sqlBuilder.append(" where ");
        for (Iterator<IWhere> it = wheres.iterator(); it.hasNext(); ) {
            sqlBuilder.append(it.next());
            sqlBuilder.append(" and ");
        }
        if (wheres.size() > 0)
            sqlBuilder.delete(sqlBuilder.length() - 5, sqlBuilder.length());
        excute(sqlBuilder.toString());
        return this;
    }

    @Override
    public List<T> findAll() {
        return values;
    }

    @Override
    public void findEnum(Type enumType) {
        Class<?> cls = (Class<?>) enumType;
    }

    @Override
    public T findOne() {
        if (values.size() > 0) return values.get(0);
        return null;
    }

    @Override
    public int status() {
        return lastUpdateCount;
    }

    @Override
    public String getTableName(Class<?> clz) {
        Table table = clz.getAnnotation(Table.class);
        String defaultTableName = prefix + clz.getSimpleName().replace("Entity", "").replace("Pojo", "").replace("Dto", "");
        return table != null ? table.value() : defaultTableName;
    }

    @Override
    public String getSimpleTableName(Class<?> clz) {
        String tableName = getTableName(clz);
        tableName = tableName.replace(prefix,"");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tableName.length(); i++) {
            char c = tableName.charAt(i);
            if(i == 0 || i==1)stringBuilder.append(c);
            else if (Character.isUpperCase(c)) {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void excute(String sql) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            boolean ret = statement.execute(sql);
            lastUpdateCount = statement.getUpdateCount();
            if (ret) {
                values.clear();
                resultSet = statement.getResultSet();

                if (EnumValue.class.isAssignableFrom(cls)) {
                    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                    int count = resultSetMetaData.getColumnCount();
                    while (resultSet.next()) {
                        String baseName = cls.getSimpleName().toLowerCase();
                        String enumName = replaceFieldName(baseName.concat("Name"));
                        String enumValue = replaceFieldName(baseName.concat("Value"));
                        EnumValue e = (EnumValue) Enum.valueOf((Class<? extends Enum>) cls, resultSet.getString(enumName));
                        e.setValue(resultSet.getString(enumValue));
                    }
                } else if (Map.class.isAssignableFrom(cls)) {
                    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                    int count = resultSetMetaData.getColumnCount();
                    while (resultSet.next()) {
                        Map<String, Object> result = new HashMap<String, Object>();
                        for (int i = 1; i <= count; i++) {
                            String columnName = resultSetMetaData.getColumnLabel(i);
                            result.put(columnName, resultSet.getString(columnName));
                        }
                        values.add((T) result);
                    }
                } else if (String.class.isAssignableFrom(cls) || Number.class.isAssignableFrom(cls)) {
                    while (resultSet.next()) {
                        String value = resultSet.getString(1);
                        Convert convert = baseDatabase.getConverts().get(cls);
                        values.add((T) convert.reconvert(value));
                    }
                } else {
                    Set<FieldModel> sets = ParamsUtil.getField(cls);
                    while (resultSet.next()) {
                        Object obj = cls.newInstance();
                        for (FieldModel field : sets) {
                            try {
                                String result = resultSet.getString(replaceFieldName(field.getName()));
                                if (isPrimary(field)) {
                                    field.getField().set(obj, Integer.valueOf(result));
                                } else {
                                    Class type = field.getField().getType();

                                    if (baseDatabase.getConverts().containsKey(type)) {
                                        Convert convert = baseDatabase.getConverts().get(type);
                                        field.getField().set(obj, convert.reconvert(result));
                                    } else if (type.isEnum()) {
                                        Convert convert = baseDatabase.getConverts().get(Enum.class);
                                        field.getField().set(obj, convert.reconvert(type, result));
                                    } else {
                                        field.getField().set(obj, result);
                                    }
                                }

                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (SQLException e) {
                                // TODO: handle exception
                            }
                        }
                        values.add((T) obj);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isPrimary(FieldModel fieldModel) {
        return fieldModel.getType().getSort() > 0 && fieldModel.getType().getSort() < 9;
    }

}
