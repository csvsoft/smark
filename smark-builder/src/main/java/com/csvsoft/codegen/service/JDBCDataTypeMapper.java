package com.csvsoft.codegen.service;

import com.csvsoft.smark.entity.FieldDataType;

import java.sql.JDBCType;
import java.util.HashMap;
import java.util.Map;

public class JDBCDataTypeMapper {
    static Map<JDBCType,FieldDataType> typeMap = new HashMap<>();
    static {init();}
    static void init() {
        /*
         STRING,BOOLEAN,BYTE,SHORT,INTEGER,LONG,FLOAT,DOUBLE,DECIMAL,DATE,TIMESTAMP;

         */
        typeMap.put(JDBCType.VARCHAR,FieldDataType.STRING);
        typeMap.put(JDBCType.INTEGER,FieldDataType.INTEGER);

        typeMap.put(JDBCType.BOOLEAN,FieldDataType.BOOLEAN);
        typeMap.put(JDBCType.TINYINT,FieldDataType.SHORT);
        typeMap.put(JDBCType.SMALLINT,FieldDataType.SHORT);
        typeMap.put(JDBCType.BIGINT,FieldDataType.LONG);


        typeMap.put(JDBCType.REAL,FieldDataType.FLOAT);
        typeMap.put(JDBCType.FLOAT,FieldDataType.DOUBLE);
        typeMap.put(JDBCType.DOUBLE,FieldDataType.DOUBLE);
        typeMap.put(JDBCType.DECIMAL,FieldDataType.DECIMAL);
        typeMap.put(JDBCType.NUMERIC,FieldDataType.DECIMAL);

        typeMap.put(JDBCType.DATE,FieldDataType.DATE);
        typeMap.put(JDBCType.TIMESTAMP,FieldDataType.TIMESTAMP);
        typeMap.put(JDBCType.TIME,FieldDataType.TIMESTAMP);


    }
    public static FieldDataType getFieldDataType(int jdbcType){
        return typeMap.get(JDBCType.valueOf(jdbcType));
    }

}
