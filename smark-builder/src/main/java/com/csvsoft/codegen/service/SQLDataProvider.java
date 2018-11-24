package com.csvsoft.codegen.service;

import com.csvsoft.smark.entity.FieldMetaData;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;

import java.util.List;
import java.util.stream.Stream;

public class SQLDataProvider extends AbstractBackEndDataProvider<List<Object>,String> {

    String sql = null;
    SQLService sqlService;
    public SQLDataProvider(SQLService sqlService){

        this.sqlService = sqlService;
    }
    public void setSQL(String sql){
        this.sql = sql;
    }
    @Override
    protected Stream<List<Object>> fetchFromBackEnd(Query<List<Object>, String> query) {
        Stream<List<Object>> listStream = sqlService.fetchObjects(sql, query.getFilter().orElseGet(() -> ""), query.getLimit(), query.getOffset(), query.getSortOrders());
        return listStream;
    }

    @Override
    protected int sizeInBackEnd(Query<List<Object>, String> query) {
        Long count =sqlService.getCount(sql, query.getFilter().orElseGet(() -> ""), query.getLimit(), query.getOffset(), query.getSortOrders());

        return count.intValue();
    }

    public List<FieldMetaData> getFieldMetaData(){
        return this.sqlService.getMetaData(this.sql);
    }
}
