package com.csvsoft.smark.service.sqlsuggestor;

import com.csvsoft.smark.entity.DummyField;
import com.csvsoft.smark.entity.FunctionSignature;
import com.csvsoft.smark.entity.IField;
import com.csvsoft.smark.sevice.ICatalogueProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GenericJDBCCataLogProvider implements ICatalogueProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericJDBCCataLogProvider.class);

    Connection con;
    private List<String> tables;
    private Map<String,IField[]> tableColumnMap = new HashMap<>();

    public GenericJDBCCataLogProvider(Connection con) {
        this.con = con;
        refresh();
    }

    @Override
    public void refresh() {
        this.tables = null;
        this.tables = getAllTables();
        tableColumnMap.clear();
        for(String table:tables){
            tableColumnMap.put(table.toUpperCase(),getFields(table));
        }
    }

    @Override
    public List<String> getAllTables() {
        if(tables != null){
            return tables;
        }
        List<String> tables = new LinkedList<>();
        try {
            DatabaseMetaData databaseMetaData = con.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});

            while (resultSet.next()) {
                tables.add(resultSet.getString("TABLE_NAME").toUpperCase());
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to get tables.", ex);
        }
        return tables;
    }

    @Override
    public IField[] getFields(String tableName) {
        String upperTable = tableName.toUpperCase();
        if(tableColumnMap.containsKey(upperTable)){
            return this.tableColumnMap.get(upperTable);
        }
        LOGGER.info("Getting columns for table:"+ tableName);
        List<IField> fieldList = new LinkedList<>();
        try {
            DatabaseMetaData databaseMetaData = con.getMetaData();
            ResultSet columns = databaseMetaData.getColumns(null, null, tableName, null);
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
               int jdbcType= columns.getInt("DATA_TYPE");

                String datatype = JDBCType.valueOf(jdbcType).getName();
                String columnsize = columns.getString("COLUMN_SIZE");
                String decimaldigits = columns.getString("DECIMAL_DIGITS");
                String isNullable = columns.getString("IS_NULLABLE");
                String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
               // String remark = columns.getString("REMARK");
                IField field= new DummyField(columnName,datatype,"","","YES".equalsIgnoreCase(isNullable)?true:false);
                fieldList.add(field);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to get table columns", ex);
        }
        LOGGER.info("total fields:"+fieldList.size());
        return fieldList.toArray(new IField[0]);
    }

    @Override
    public List<FunctionSignature> getAllFunctions() {
        return new LinkedList<FunctionSignature>();
    }

    @Override
    public boolean isTable(String name) {
        return tables.contains(name.toUpperCase());
    }

    @Override
    public boolean isView(String name) {
        return tables.contains(name.toUpperCase());
    }

    @Override
    public boolean isFunction(String name) {
        return false;
    }
}
