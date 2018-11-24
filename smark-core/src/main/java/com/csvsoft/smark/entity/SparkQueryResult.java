package com.csvsoft.smark.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SparkQueryResult implements Serializable {
    private List<FieldMetaData> fieldMetaDataList;
    private List<Map<String,String>> dataList;

    public List<FieldMetaData> getFieldMetaDataList() {
        return fieldMetaDataList;
    }

    public void setFieldMetaDataList(List<FieldMetaData> fieldMetaDataList) {
        this.fieldMetaDataList = fieldMetaDataList;
    }

    public List<Map<String, String>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }
}
