package com.csvsoft.codegen.service;

import com.csvsoft.smark.entity.FieldMetaData;
import com.vaadin.data.provider.QuerySortOrder;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Stream;

public abstract class SQLService {


    abstract public Stream<List<Object>> fetchObjects(String sql, String filter, int limit, int offset, List<QuerySortOrder> sortOrders) ;
    abstract public Long getCount(String sql, String filter, int limit, int offset, List<QuerySortOrder> sortOrders) ;

    protected String getRunSQLNoLimitOffSet(String sql, String filter, int limit, int offset, List<QuerySortOrder> sortOrders){
        String finalSql = "SELECT * FROM ("+ sql + ") a1";
        if (StringUtils.isNotBlank(filter)){
            finalSql = finalSql + " WHERE " + filter;
        }
        if (sortOrders != null && !sortOrders.isEmpty()){
            finalSql = finalSql +  SortStringGenerator.generate(sortOrders) + " ";
        }
        return finalSql;
    }
    abstract public List<FieldMetaData> getMetaData(String sql);
}
