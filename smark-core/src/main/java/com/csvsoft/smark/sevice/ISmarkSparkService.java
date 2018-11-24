package com.csvsoft.smark.sevice;

import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.entity.SparkCatalogContainer;
import com.csvsoft.smark.entity.SparkQueryResult;
import com.csvsoft.smark.exception.SmarkSparkServiceException;

import java.util.List;
import java.util.Map;

public interface ISmarkSparkService {

    public SparkCatalogProvider executeSpec(long runId, String runTo, String specXML,String appProp) throws SmarkSparkServiceException;
    public SparkCatalogProvider executeSpec(long runId, String runTo, SmarkAppSpec appSpec, String appProp) throws SmarkSparkServiceException ;
    public byte[] executeSpecRemote(long runId, String runTo, byte[] specBytes,String appProp) throws SmarkSparkServiceException;
    public byte[] executeSpecRemote(long runId, String runTo, String specXML,String appProp) throws SmarkSparkServiceException;

    public SparkQueryResult executeSQL(String sql,int maxRows) throws SmarkSparkServiceException;
    public byte[] executeSQLRemote(String sql, int maxRows) throws SmarkSparkServiceException;
}
