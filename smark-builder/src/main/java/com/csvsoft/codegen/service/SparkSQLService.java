package com.csvsoft.codegen.service;

import com.csvsoft.smark.entity.FieldMetaData;
import com.csvsoft.smark.entity.StructFieldTypeMapper;
import com.vaadin.data.provider.QuerySortOrder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SparkSQLService extends SQLService {

    SparkSession sparkSession;

    public SparkSQLService(SparkSession sparkSession){
        this.sparkSession = sparkSession;
    }

    public Long getCount(String sql, String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {
        String finalSQL = this.getRunSQLNoLimitOffSet(sql,filter,limit,offset,sortOrders);
        String countSQL = "select count(1) from (" + finalSQL + ")";
        Row[] row1 =(Row[]) sparkSession.sql(countSQL).take(1);
        return row1[0].getLong(0);
    }


        public Stream<List<Object>> fetchObjects(String sql, String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {

        String finalSQL = this.getRunSQLNoLimitOffSet(sql,filter,limit,offset,sortOrders);
        Dataset<Row> rowDataSet = sparkSession.sql(finalSQL);
        Stream<List<Object>> result = rowDataSet.takeAsList(limit).stream().map(row -> {
            int cols = row.length();
            List<Object> colObjects = new ArrayList<>(cols);
            for (int i = 0; i < cols; i++) {
                colObjects.add(row.get(i));
            }
            return colObjects;
        });
        return result;

    }

    @Override
    public List<FieldMetaData> getMetaData(String sql) {
        String simpleSQL = "select * from (" + sql + ") where 1 = 0 ";
        Dataset<Row> rowDataSet = sparkSession.sql(simpleSQL);
        List<FieldMetaData> fieldTypeList = Arrays.stream(rowDataSet.schema().fields()).map(structField -> {
            FieldMetaData fm = new FieldMetaData();
            fm.setName(structField.name());
            Class fieldClass= structField.dataType().getClass();
            fm.setDataType(StructFieldTypeMapper.getFieldDataType(structField.dataType().getClass().getName()));
            return fm;
        }).collect(Collectors.toList());

        return fieldTypeList;
    }
}
