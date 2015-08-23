package com.opdar.framework.db.impl;

import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.db.anotations.Factor;
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
    private MappingFilter filter;

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
    private Map<String, FieldModel> fieldSort = new LinkedHashMap<String, FieldModel>();
    String tableName = "";
    private String simpleTableName = "";
    private boolean autoCloseConnection = true;

    static final Map<String,Map<String, FieldModel>> models = new HashMap<String, Map<String, FieldModel>>();

    public BaseDaoImpl(DataSource dataSource, Class<T> cls, BaseDatabase baseDatabase) {
        this.dataSource = dataSource;
        this.sqlBuilder = new StringBuilder();
        this.cls = cls;
        this.baseDatabase = baseDatabase;
        if(!EnumValue.class.isAssignableFrom(cls)){
            SeedInvoke.init(cls);
            if(BaseDaoImpl.models.containsKey(cls.getName())){
                fieldSort = BaseDaoImpl.models.get(cls.getName());
            }else{
                java.lang.reflect.Field[] fields = cls.getDeclaredFields();
                for(int i = 0;i<fields.length;i++){
                    java.lang.reflect.Field field = fields[i];
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    if (fieldName.equals("serialVersionUID")) continue;
                    ChildSql childSql = executeChildSql(field);
                    FieldModel model = new FieldModel(field.getType(),childSql,field);
                    this.fieldSort.put(fieldName, model);
                    Field fieldAnnotation = field.getAnnotation(Field.class);
                    if (fieldAnnotation != null){
                        if(fieldAnnotation.ignore())
                            continue;
                        if(fieldAnnotation.value() != null && fieldAnnotation.value().trim().length() > 0){
                            model.setMapping(fieldAnnotation.value());
                            continue;
                        }
                    }else{
                        model.setMapping(replaceFieldName(fieldName));
                    }
                }
                BaseDaoImpl.models.put(cls.getName(),fieldSort);
            }
        }
        tableName = getTableName(cls);
        simpleTableName = getSimpleTableName(cls);
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setAutoCloseConnection(boolean autoCloseConnection) {
        this.autoCloseConnection = autoCloseConnection;
    }

    private ChildSql executeChildSql(java.lang.reflect.Field field){
        Factor factor = field.getAnnotation(Factor.class);
        if(factor!=null){
            ChildSql childSql = new ChildSql();
            childSql.sql = factor.value();
            childSql.type = factor.cls();
            childSql.sql = Utils.parseSignFactor(childSql.sql, childSql.parentMapping);
            return childSql;
        }
        return null;
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
        if(!mapper.contains(mapper))
            this.mapper.add(_mapper);
        return this;
    }

    @Override
    public IDao<T> clearMapper(){
        this.mapper.clear();
        return this;
    }

    private void close(){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

    private HashMap<String,IDao> extendDaos = new HashMap<String, IDao>();

    @Override
    public <D>IDao<D> extend(Class<D> clz) {
        BaseDaoImpl dao = (BaseDaoImpl) baseDatabase.getDao(clz);
        dao.connection = this.connection;
        extendDaos.put(clz.getName(),dao);
        return dao;
    }

    @Override
    public IDao<T> closeExtend() {
        for(Iterator<String> it = extendDaos.keySet().iterator();it.hasNext();){
            String key = it.next();
            BaseDaoImpl dao = (BaseDaoImpl) extendDaos.get(key);
            dao.connection = null;
            extendDaos.remove(key);
        }
        return this;
    }

    @Override
    public IDao<T> insert(T o) {
        clear();
        if (tableName != null) {
            StringBuilder valueBuilder = new StringBuilder();
            sqlBuilder.append("insert into ");
            sqlBuilder.append(tableName);
            sqlBuilder.append(" (");
            for (Iterator<Map.Entry<String,FieldModel>> it = fieldSort.entrySet().iterator();it.hasNext();) {
                Map.Entry<String, FieldModel> entry = it.next();
                String fieldName = entry.getKey();
                FieldModel model = entry.getValue();
                String dbFieldName = model.getMapping();
                Class<?> fieldType = model.getType();
                java.lang.reflect.Field field = model.getField();
                try {
                    Object value = field.get(o);
                    if(value != null){
                        sqlBuilder.append("`").append(dbFieldName).append("`");
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
    public IDao<T> join(Join join, String tableName, String synx) {
        sqlBuilder.append(" ").append(join.name()).append(" JOIN ").append(tableName).append(" ON ").append(synx);
        return this;
    }

    @Override
    public IDao<T> update(T o) {
        clear();
        if (tableName != null) {
            sqlBuilder.append("update ").append(tableName).append(" set ");
            StringBuilder values = new StringBuilder();
            for (Iterator<Map.Entry<String,FieldModel>> it = fieldSort.entrySet().iterator();it.hasNext();) {
                Map.Entry<String, FieldModel> entry = it.next();
                FieldModel model = entry.getValue();
                String dbFieldName = model.getMapping();
                java.lang.reflect.Field field = model.getField();
                try {
                    Object value = field.get(o);
                    if(value != null)
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
    public IDao<T> delete() {
        clear();
        sqlBuilder.append("DELETE FROM ").append(tableName);
        return this;
    }

    @Override
    public IDao<T> setFilter(MappingFilter filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public IDao select() {
        clear();
        if (tableName != null) {
            sqlBuilder.append("select  ");
            for(String map:mapper){
                sqlBuilder.append(map).append(" ,");
            }

            String simpleTableName = this.simpleTableName+".";
            if (EnumValue.class.isAssignableFrom(cls)) {
                String baseName = cls.getSimpleName().toLowerCase();
                String enumName = replaceFieldName(baseName.concat("Name"));
                String enumValue = replaceFieldName(baseName.concat("Value "));
                sqlBuilder.append(simpleTableName+enumName).append(",").append(simpleTableName+enumValue);
            } else {
                for (Iterator<Map.Entry<String,FieldModel>> it = fieldSort.entrySet().iterator();it.hasNext();) {
                    Map.Entry<String, FieldModel> entry = it.next();
                    FieldModel model = entry.getValue();
                    String dbFieldName = model.getMapping();
                    if(filter!=null && filter.getFilter().contains(dbFieldName.toUpperCase()))continue;
                    String complateName = simpleTableName + dbFieldName;
                    if(filter!=null && filter.getRedefinItionField().containsKey(dbFieldName.toUpperCase())){
                        complateName = filter.getRedefinItionField().get(dbFieldName.toUpperCase());
                    }
                    sqlBuilder.append(complateName);
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.delete(sqlBuilder.length() - 1, sqlBuilder.length());
            sqlBuilder.append(" from ");
            sqlBuilder.append(tableName).append(" ").append(this.simpleTableName);
        }
        return this;
    }

    @Override
    public IDao<T> count() {
        clear();
        if (tableName != null) {
            sqlBuilder.append("select count(1) from ").append(tableName).append(" ").append(this.simpleTableName);
        }
        return this;
    }

    @Override
    public IWhere<T> where(String name, String value) {
        BaseWhere baseWhere = new BaseWhere();
        baseWhere.IS(name, value);
        return where(baseWhere);
    }

    @Override
    public IWhere<T> where(IWhere where) {
        where.setDao(this);
        wheres.add(where);
        return where;
    }

    @Override
    public IDao<T> end() {
        end(cls);
        return this;
    }

    @Override
    public IDao<T> end(Class<?> cls) {
        if (wheres.size() > 0) sqlBuilder.append(" where ");
        for (Iterator<IWhere> it = wheres.iterator(); it.hasNext(); ) {
            sqlBuilder.append(it.next());
            sqlBuilder.append(" and ");
        }
        if (wheres.size() > 0)
            sqlBuilder.delete(sqlBuilder.length() - 5, sqlBuilder.length());
        excute(sqlBuilder.toString(),cls);
        filter = null;
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
    public Map<String, FieldModel> getFieldNames() {
        return fieldSort;
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
        int index = -1;
        if((index= tableName.lastIndexOf(".")) != -1){
            tableName = tableName.substring(index+1);
        }
        tableName = tableName.replace(prefix,"");
        return "`".concat(tableName).concat("`");
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
    public IDao<T> rollback() throws SQLException {
        if(connection!=null){
            connection.rollback();
            connection.close();
            connection = null;
        }
        return this;
    }

    @Override
    public void excute(String sql) {
        excute(sql,cls);
    }

    @Override
    public void excute(String sql,Class<?> cls) {
        System.out.println(sql);
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
                        try{
                            EnumValue e = (EnumValue) Enum.valueOf((Class<? extends Enum>) cls, resultSet.getString(enumName));
                            e.setValue(resultSet.getString(enumValue));
                        }catch (Exception e){
                        }
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
                                for (Iterator<Map.Entry<String,FieldModel>> it = fieldSort.entrySet().iterator();it.hasNext();) {
                                    try {
                                        Map.Entry<String, FieldModel> entry = it.next();
                                        String fieldName = entry.getKey();
                                        String setMethodName = "set".concat(Utils.testField(fieldName));
                                        FieldModel model = entry.getValue();
                                        if(model.getChildSql() !=null){
                                            ChildSql childSql = model.getChildSql();
                                            String[] pMap = new String[childSql.parentMapping.size()];
                                            int pIndex = 0;
                                            for(String s:childSql.parentMapping){
                                                if(fieldSort.containsKey(s)){
                                                    FieldModel m2 = fieldSort.get(s);
                                                    try {
                                                        String result = resultSet.getString(m2.getMapping());
                                                        pMap[pIndex] = "'".concat(result).concat("'");
                                                    }catch (Exception e){
                                                        throw new Exception(e.getCause());
                                                    }
                                                }else{
                                                    pMap[pIndex] = "''";
                                                }
                                                pIndex++;
                                            }
                                            String complateSql = String.format(childSql.sql,pMap);
                                            BaseDaoImpl dap = new BaseDaoImpl(dataSource,childSql.type,baseDatabase);
                                            dap.setConnection(connection);
                                            dap.setAutoCloseConnection(false);
                                            dap.excute(complateSql);
                                            Object result = null;
                                            if(Collection.class.isAssignableFrom(model.field.getType())){
                                                result = dap.findAll();
                                            }else{
                                                result = dap.findOne();
                                            }
                                            object.invokeMethod(setMethodName,result );
                                            continue;
                                        }
                                        String dbFieldName = model.getMapping();
                                        try{
                                            resultSet.findColumn(dbFieldName);
                                        }catch (Exception e){
                                            continue;
                                        }
                                        String result = resultSet.getString(dbFieldName);
                                        java.lang.reflect.Field field = model.getField();

                                        Class type = field.getType();
                                        if (PrimaryUtil.isPrimary(field.getType())) {
                                            object.invokeMethod(setMethodName, PrimaryUtil.cast(result, type));
                                        } else {
                                            if (baseDatabase.getConverts().containsKey(type)) {
                                                Convert convert = baseDatabase.getConverts().get(type);
                                                object.invokeMethod(setMethodName, convert.reconvert(result));
                                            } else if (type.isEnum()) {
                                                Convert convert = baseDatabase.getConverts().get(Enum.class);
                                                object.invokeMethod(setMethodName, convert.reconvert(type, result));
                                            } else {
                                                object.invokeMethod(setMethodName, result);
                                            }
                                        }
                                    } catch (SQLException e2) {
                                        e2.printStackTrace();
                                    } catch (Exception e2) {
                                    }
                                }
                                values.add((T) object);
                            } catch (Exception e2) {
//                                e2.printStackTrace();
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
                if (this.connection==null && connection != null && autoCloseConnection)
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
