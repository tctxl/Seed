package com.opdar.framework.db.impl;

import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.db.anotations.Field;
import com.opdar.framework.db.anotations.Table;
import com.opdar.framework.db.convert.Convert;
import com.opdar.framework.db.interfaces.EnumValue;
import com.opdar.framework.db.interfaces.IDao;
import com.opdar.framework.db.interfaces.IWhere;
import com.opdar.framework.utils.PrimaryUtil;
import com.opdar.framework.utils.Utils;

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
    private Connection connection;

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
    private Map<Integer, java.lang.reflect.Field> fields = new HashMap<Integer, java.lang.reflect.Field>();
    private Map<Integer, String> fieldMapping = new HashMap<Integer, String>();
    private Map<String, Integer> fieldSort = new HashMap<String, Integer>();
    private Map<Integer, Class<?>> fieldTyps = new HashMap<Integer, Class<?>>();
    String tableName = "";
    public BaseDaoImpl(DataSource dataSource, Class<T> cls, BaseDatabase baseDatabase) {
        this.dataSource = dataSource;
        this.sqlBuilder = new StringBuilder();
        this.cls = cls;
        this.baseDatabase = baseDatabase;
        SeedInvoke.init(cls);
        java.lang.reflect.Field[] fields = cls.getDeclaredFields();
        for(int i = 0;i<fields.length;i++){
            java.lang.reflect.Field field = fields[i];
            field.setAccessible(true);
            String fieldName = field.getName();
            if (fieldName.equals("serialVersionUID")) continue;
            this.fieldSort.put(fieldName, i);
            this.fields.put(i,field);
            Field fieldAnnotation = field.getAnnotation(Field.class);
            fieldTyps.put(i, field.getType());
            if (fieldAnnotation != null){
                if(fieldAnnotation.ignore())
                    continue;
                if(fieldAnnotation.value() != null && fieldAnnotation.value().trim().length() > 0){
                    fieldMapping.put(i,fieldAnnotation.value());
                    continue;
                }
            }else{
                fieldMapping.put(i,replaceFieldName(fieldName));
            }
        }
        tableName = getTableName(cls);
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
    public IDao<T> INSERT(T o) {
        clear();
        if (tableName != null) {
            StringBuilder valueBuilder = new StringBuilder();
            sqlBuilder.append("insert into ");
            sqlBuilder.append(tableName);
            sqlBuilder.append(" (");
            for (Iterator<Map.Entry<String,Integer>> it = fieldSort.entrySet().iterator();it.hasNext();) {
                Map.Entry<String, Integer> entry = it.next();
                String fieldName = entry.getKey();
                Integer fieldSort = entry.getValue();
                String dbFieldName = fieldMapping.get(fieldSort);
                Class<?> fieldType = fieldTyps.get(fieldSort);
                java.lang.reflect.Field field = fields.get(fieldSort);
                try {
                    Object value = field.get(o);
                    if(value != null){
                        sqlBuilder.append(dbFieldName);
                        sqlBuilder.append(",");
                        valueBuilder.append("'");
                        valueBuilder.append(value);
                        valueBuilder.append("'");
                        valueBuilder.append(",");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
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
        if (tableName != null) {
            sqlBuilder.append("update ").append(tableName).append(" set ");
            StringBuilder values = new StringBuilder();
            for (Iterator<Map.Entry<String,Integer>> it = fieldSort.entrySet().iterator();it.hasNext();) {
                Map.Entry<String, Integer> entry = it.next();
                Integer fieldSort = entry.getValue();
                String dbFieldName = fieldMapping.get(fieldSort);
                java.lang.reflect.Field field = fields.get(fieldSort);
                try {
                    Object value = field.get(o);
                    values.append(dbFieldName).append("=").append("'").append(value).append("'").append(",");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if(values.length() > 0 ){
                values.delete(values.length() - 1, values.length());
                sqlBuilder.append(values);
            }else{
                clear();
            }
        }
        return this;
    }

    @Override
    public IDao<T> DELETE(Object o) {
        clear();
        return this;
    }

    @Override
    public IDao SELECT() {
        clear();
        if (tableName != null) {
            sqlBuilder.append("select  ");
            for(String map:mapper){
                sqlBuilder.append(map).append(" ,");
            }
            String simpleTableName = getSimpleTableName(cls)+".";
            if (EnumValue.class.isAssignableFrom(cls)) {
                String baseName = cls.getSimpleName().toLowerCase();
                String enumName = replaceFieldName(baseName.concat("Name"));
                String enumValue = replaceFieldName(baseName.concat("Value "));
                sqlBuilder.append(simpleTableName+enumName).append(",").append(simpleTableName+enumValue);
            } else {
                for (Iterator<Map.Entry<String,Integer>> it = fieldSort.entrySet().iterator();it.hasNext();) {
                    Map.Entry<String, Integer> entry = it.next();
                    Integer fieldSort = entry.getValue();
                    String dbFieldName = fieldMapping.get(fieldSort);
                    sqlBuilder.append(simpleTableName + dbFieldName);
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.delete(sqlBuilder.length() - 1, sqlBuilder.length());
            sqlBuilder.append(" from ");
            sqlBuilder.append(tableName).append(" ").append(getSimpleTableName(cls));
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
        String defaultTableName = prefix + clz.getSimpleName().toUpperCase().replace("ENTITY", "").replace("POJO", "").replace("DTO", "").replace("BEAN","");
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
    public IDao<T> openTransaction() throws SQLException {
        connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        return this;
    }

    @Override
    public IDao<T> commit() throws SQLException {
        if(connection!=null){
            connection.commit();
            connection.close();
            connection = null;
        }
        return this;
    }

    @Override
    public void excute(String sql) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            if(this.connection !=null){
                connection = this.connection;
            }else{
                connection = dataSource.getConnection();
            }
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
                    try {
                        while (resultSet.next()) {
                            try {
                                SeedExcuteItrf object = SeedInvoke.buildObject(cls);
                                for (Iterator<Map.Entry<String,Integer>> it = fieldSort.entrySet().iterator();it.hasNext();) {
                                    try {
                                        Map.Entry<String, Integer> entry = it.next();
                                        String fieldName = entry.getKey();
                                        Integer fieldSort = entry.getValue();
                                        String dbFieldName = fieldMapping.get(fieldSort);
                                        String result = resultSet.getString(dbFieldName);
                                        java.lang.reflect.Field field = fields.get(fieldSort);

                                        Class type = field.getType();
                                        if (PrimaryUtil.isPrimary(field.getType())) {
                                            object.invokeMethod("set" + Utils.testField(fieldName), PrimaryUtil.cast(result, type));
                                        } else {
                                            if (baseDatabase.getConverts().containsKey(type)) {
                                                Convert convert = baseDatabase.getConverts().get(type);
                                                object.invokeMethod("set" + Utils.testField(fieldName), convert.reconvert(result));
                                            } else if (type.isEnum()) {
                                                Convert convert = baseDatabase.getConverts().get(Enum.class);
                                                object.invokeMethod("set" + Utils.testField(fieldName), convert.reconvert(type, result));
                                            } else {
                                                object.invokeMethod("set" + Utils.testField(fieldName), result);
                                            }
                                        }

                                        values.add((T) object);
                                    } catch (SQLException e2) {
                                        e2.printStackTrace();
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                }
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (this.connection==null && connection != null)
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

}
