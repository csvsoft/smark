package com.csvsoft.codegen.service;

import com.csvsoft.smark.entity.FieldMetaData;
import com.vaadin.data.provider.QuerySortOrder;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class JDBCSQLService extends SQLService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCSQLService.class);


    private Connection conn;
    public JDBCSQLService(Connection conn){
        this.conn = conn;
    }
    @Override
    public Stream<List<Object>> fetchObjects(String sql, String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {

        String runSQL = getRunSQL(sql,filter,limit,offset,sortOrders);
        QueryRunner run = new QueryRunner();
        ArrayListHandler h = new ArrayListHandler();

        List<Object[]> result = null;
        try {
            LOGGER.info("Running sql:"+runSQL);
            result = run.query(conn,runSQL, h);
            LOGGER.info("sql result count:"+ result.size());
        }catch(SQLException ex){
            throw new RuntimeException ("Unable to run SQL:" + runSQL ,ex);
        }
        return result.stream().map(objArray -> Arrays.asList(objArray));
    }

    @Override
    public Long getCount(String sql, String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {
        String runSQL= getRunSQLNoLimitOffSet(sql,filter,limit,offset,null);
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT COUNT(1) RO_COUNT FROM (");
        builder.append(runSQL).append(") ax");
        String finalSQL= builder.toString();
        QueryRunner runner = new QueryRunner();

        ArrayListHandler h = new ArrayListHandler();

        List<Object[]> result = null;
        try {
            result = runner.query(conn,finalSQL, h);
            Object[] cols = result.get(0);
            Long count=(Long)cols[0];
            return count;

        }catch(SQLException ex){
            throw new RuntimeException ("Unable to run SQL:"+finalSQL,ex );
        }
        

    }

    @Override
    public List<FieldMetaData> getMetaData(String sql) {
        String finalSQL = "select * from ("+sql+") xx where 1 = 0";

        try {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(finalSQL);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int count=metaData.getColumnCount();
            List<FieldMetaData> fmdList = new ArrayList<>(count);
            for(int i=1;i<=count;i++){
               String colName = metaData.getColumnName(i);
               int jdbcDataType = metaData.getColumnType(i);
                FieldMetaData fmd = new FieldMetaData(colName,JDBCDataTypeMapper.getFieldDataType(jdbcDataType));
                fmdList.add(fmd);
            }
            return fmdList;
        }catch(SQLException ex){
            throw new RuntimeException ("Unable to run SQL:"+finalSQL,ex );
        }

    }

    public abstract String getRunSQL(String sql, String filter, int limit, int offset, List<QuerySortOrder> sortOrders);
}
