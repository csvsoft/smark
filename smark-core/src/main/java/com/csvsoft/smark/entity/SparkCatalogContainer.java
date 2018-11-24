package com.csvsoft.smark.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SparkCatalogContainer implements Serializable {

    private String[] tables;
    private List<FunctionSignature> functionSignatures;
    private Map<String,IField[]> fieldsMap;

    public String[] getTables() {
        return tables;
    }

    public void setTables(String[] tables) {
        this.tables = tables;
    }

    public List<FunctionSignature> getFunctionSignatures() {
        return functionSignatures;
    }

    public void setFunctionSignatures(List<FunctionSignature> functionSignatures) {
        this.functionSignatures = functionSignatures;
    }

    public Map<String, IField[]> getFieldsMap() {
        return fieldsMap;
    }

    public void setFieldsMap(Map<String, IField[]> fieldsMap) {
        this.fieldsMap = fieldsMap;
    }
}
