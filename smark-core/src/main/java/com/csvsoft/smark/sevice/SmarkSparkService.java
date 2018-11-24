package com.csvsoft.smark.sevice;

import com.csvsoft.smark.entity.StructFieldTypeMapper;
import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.core.SmarkAppRunner;
import com.csvsoft.smark.entity.FieldDataType;
import com.csvsoft.smark.entity.FieldMetaData;
import com.csvsoft.smark.entity.SparkQueryResult;
import com.csvsoft.smark.exception.SmarkSparkServiceException;
import com.csvsoft.smark.util.ScalaLang;
import com.csvsoft.smark.util.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SmarkSparkService implements ISmarkSparkService {
    SparkSession spark;

    public SmarkSparkService(){
        spark = SparkSession.builder().config("spark.driver.host", "localhost").appName("test").master("local").getOrCreate();
    }

    @Override
    public byte[] executeSpecRemote(long runId, String runTo, String specXML, String appProp) throws SmarkSparkServiceException {
        try {
            SparkCatalogProvider sparkCatalogProvider = executeSpec(runId,runTo,specXML,appProp);
            byte[] sparkCatalogProviderBytes = SerializationUtils.serialize(sparkCatalogProvider);
            return sparkCatalogProviderBytes;
        }catch(Exception ex){
            throw new SmarkSparkServiceException("Unable to execute spec:"+specXML,ex);
        }
    }

    @Override
    public SparkCatalogProvider executeSpec(long runId, String runTo, String specXML,String appProp) throws SmarkSparkServiceException {

        int[] runTos=getRunTo(runTo);
        SmarkAppRunner.run(spark,runId, runTos[0],runTos[1],specXML,appProp);
        return new SparkCatalogProvider(spark);
    }

    private int[] getRunTo(String runTo){
        String[] runTos = StringUtils.split(runTo,".");
        int taskRunto = -1;
        int subTaskRunto = -1;
        if(runTos.length == 1){
            taskRunto= Integer.valueOf(runTos[0]);
        }else if(runTos.length >= 2){
            taskRunto= Integer.valueOf(runTos[0]);
            subTaskRunto= Integer.valueOf(runTos[1]);
        }
        return new int[]{taskRunto,subTaskRunto};
    }
   // @Override
    public SparkCatalogProvider executeSpec(long runId, String runTo, byte[] specBytes,String appProp) throws SmarkSparkServiceException {
        int[] runTos=getRunTo(runTo);
        SmarkAppRunner.run(spark,runId, runTos[0], runTos[1],specBytes,appProp);
        return new SparkCatalogProvider(spark);
    }

    @Override
    public SparkQueryResult executeSQL(String sql,int maxRows) throws SmarkSparkServiceException {
        Dataset<Row> result = spark.sql(sql);
        List<Row> rows = result.takeAsList(maxRows);
        StructType schema = result.schema();
        List<FieldMetaData> fieldTypeList = Arrays.stream(schema.fields()).map(structField -> {
            FieldMetaData fm = new FieldMetaData();
            fm.setName(structField.name());
            Class fieldClass= structField.dataType().getClass();
            fm.setDataType(StructFieldTypeMapper.getFieldDataType(structField.dataType().getClass().getName()));
            return fm;
        }).collect(Collectors.toList());

        //data
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        List<Map<String,String>> resultList = rows.stream().map(row -> {
            int cols = row.length();
            Map<String,String> colObjects = new HashMap<>(cols);
            for (int i = 0; i < cols; i++) {
                FieldMetaData fm = fieldTypeList.get(i);
                String colValue= "";
                if(FieldDataType.DATE.equals(fm.getDataType()) ){
                    Date date = row.getDate(i);
                    if(date!= null){
                        colValue = sdf.format(date);
                    }

                }else  if(FieldDataType.TIMESTAMP.equals(fm.getDataType()) ){
                    Timestamp date = row.getTimestamp(i);
                    if(date!= null){
                        colValue = sdf.format(date);
                    }

                }else {
                   Object obj = row.get(i);
                   if(obj!=null){
                       colValue = obj.toString();
                   }
                }

                colObjects.put(fieldTypeList.get(i).getName(),colValue);
            }
            return colObjects;
        }).collect(Collectors.toList());


        SparkQueryResult sr = new SparkQueryResult();
        sr.setFieldMetaDataList(fieldTypeList);
        sr.setDataList(resultList);
        return sr;
    }

    @Override
    public byte[] executeSpecRemote(long runId, String runTo, byte[] specBytes,String appProp) throws SmarkSparkServiceException{
        try {
            SmarkAppSpec smarkAppSpec = SerializationUtils.deserialize(specBytes, SmarkAppSpec.class);
            SparkCatalogProvider sparkCatalogProvider = executeSpec(runId, runTo, smarkAppSpec, appProp);
            byte[] bytes = SerializationUtils.serialize(sparkCatalogProvider);
            return bytes;
        }catch(Exception ex){
            throw new SmarkSparkServiceException("Unable to execute smark spec:",ex);
        }
    }
    public SparkCatalogProvider executeSpec(long runId, String runTo, SmarkAppSpec appSpec, String appProp) throws SmarkSparkServiceException {
        int[] runTos=getRunTo(runTo);
        SmarkAppRunner.run(spark,runId, runTos[0], runTos[1],appSpec,appProp, ScalaLang.none());
        return new SparkCatalogProvider(spark);
    }


    public byte[] executeSQLRemote(String sql,int maxRows) throws SmarkSparkServiceException{
        SparkQueryResult sparkQueryResult = this.executeSQL(sql, maxRows);
        ByteArrayOutputStream out =new ByteArrayOutputStream();
        try {
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(sparkQueryResult);
        }catch(IOException ex){
            throw new SmarkSparkServiceException("obj serialization error",ex);
        }
        byte[] bytes= out.toByteArray();
        return bytes;

    }

}
