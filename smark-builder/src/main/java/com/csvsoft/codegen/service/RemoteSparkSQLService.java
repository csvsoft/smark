package com.csvsoft.codegen.service;

import com.csvsoft.smark.entity.FieldMetaData;
import com.csvsoft.smark.entity.SparkQueryResult;
import com.csvsoft.smark.exception.SmarkRunTimeException;
import com.csvsoft.smark.exception.SmarkSparkServiceException;
import com.csvsoft.smark.service.SmarkAppMiniServer;
import com.vaadin.data.provider.QuerySortOrder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoteSparkSQLService extends SQLService {


    SmarkAppMiniServer miniServer;

    public RemoteSparkSQLService(SmarkAppMiniServer miniServer) {
        this.miniServer = miniServer;
    }

    public Long getCount(String sql, String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {
        String finalSQL = this.getRunSQLNoLimitOffSet(sql, filter, limit, offset, sortOrders);
        String countSQL = "select count(1) rowCount from (" + finalSQL + ")";

        try {
            SparkQueryResult queryResult = miniServer.executeSQLRemote(countSQL, 1);
            List<Map<String, String>> dataList = queryResult.getDataList();
            if (dataList.size() != 1) {
                throw new SmarkRunTimeException("Count query returned wrong row count, expected 1:" + dataList.size());
            }
            String rowCountStr = dataList.get(0).get("rowCount");
            return Long.valueOf(rowCountStr);
        } catch (SmarkSparkServiceException e) {
            throw new SmarkRunTimeException("Remote spark SQL error:", e);
        }

    }


    public Stream<List<Object>> fetchObjects(String sql, String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {

        String finalSQL = this.getRunSQLNoLimitOffSet(sql, filter, limit, offset, sortOrders);
        try {
            SparkQueryResult queryResult = miniServer.executeSQLRemote(finalSQL, limit);
            List<Map<String, String>> dataList = queryResult.getDataList();
            List<FieldMetaData> fieldMetaDataList = queryResult.getFieldMetaDataList();
            Stream<List<Object>> listStream = dataList.stream().map(map -> {
                List<Object> rowDataList = new ArrayList<>(fieldMetaDataList.size());
                fieldMetaDataList.forEach(fmd -> rowDataList.add(map.get(fmd.getName())));
                return rowDataList;

            });
            return listStream;
        } catch (SmarkSparkServiceException e) {
            throw new SmarkRunTimeException("Remote spark SQL error:", e);
        }
    }

    @Override
    public List<FieldMetaData> getMetaData(String sql) {
        String simpleSQL = "select * from (" + sql + ") where 1 = 0 ";
        try {
            SparkQueryResult queryResult = miniServer.executeSQLRemote(simpleSQL, 0);
            List<FieldMetaData> fieldMetaDataList = queryResult.getFieldMetaDataList();
            return fieldMetaDataList;
        } catch (SmarkSparkServiceException e) {
            throw new SmarkRunTimeException("Remote spark SQL error:", e);
        }
    }
}
