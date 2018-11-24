package com.csvsoft.smark.entity;

import java.io.Serializable;

public class FieldMetaData implements Serializable{

    private String name;
    private FieldDataType dataType;

    public FieldMetaData(){}
    public FieldMetaData(String name, FieldDataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FieldDataType getDataType() {
        return dataType;
    }

    public void setDataType(FieldDataType dataType) {
        this.dataType = dataType;
    }
}
