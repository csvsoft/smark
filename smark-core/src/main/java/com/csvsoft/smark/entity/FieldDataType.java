package com.csvsoft.smark.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public enum FieldDataType implements Serializable{
    STRING("String")
    ,BOOLEAN("Boolean")
    ,BYTE("Byte")
    ,SHORT("Short")
    ,INTEGER("Integer")
    ,LONG("Long")
    ,FLOAT("Float")
    ,DOUBLE("Double")
    ,DECIMAL("Decimal")
    ,DATE("Date")
    ,TIMESTAMP("TimeStamp");
    String value;
    FieldDataType(String value){
        this.value = value;
    }
    public String getValue(){
        return this.value;
    }
    public List<FieldDataType> getAllDataType(){
        List<FieldDataType> list = new LinkedList<>();
        list.add(FieldDataType.STRING);
        list.add(FieldDataType.BOOLEAN);
        list.add(FieldDataType.BYTE);
        list.add(FieldDataType.SHORT);
        list.add(FieldDataType.INTEGER);
        list.add(FieldDataType.LONG);
        list.add(FieldDataType.FLOAT);
        list.add(FieldDataType.DOUBLE);
        list.add(FieldDataType.DECIMAL);
        list.add(FieldDataType.DATE);
        list.add(FieldDataType.TIMESTAMP);
        return list;
    }

}
