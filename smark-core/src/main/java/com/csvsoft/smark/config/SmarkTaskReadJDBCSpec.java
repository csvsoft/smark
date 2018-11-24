package com.csvsoft.smark.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SmarkTaskReadJDBCSpec extends SmarkTaskBaseJDBCSpec implements HaveViews{

    private String sql;
    private String viewName;

    private String partitionColumnName;
    private long lowerBound=-1;
    private long upperBound=-1L;
    private long numPartitions=-1;

    private long fetchsize = 1000;
    private String customSchema;

    public String getPartitionColumnName() {
        return partitionColumnName;
    }

    public void setPartitionColumnName(String partitionColumnName) {
        this.partitionColumnName = partitionColumnName;
    }

    public long getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(long lowerBound) {
        this.lowerBound = lowerBound;
    }

    public long getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(long upperBound) {
        this.upperBound = upperBound;
    }



    public long getFetchsize() {
        return fetchsize;
    }

    public void setFetchsize(long fetchsize) {
        this.fetchsize = fetchsize;
    }

    public String getCustomSchema() {
        return customSchema;
    }

    public void setCustomSchema(String customSchema) {
        this.customSchema = customSchema;
    }

    public String getViewName() {
        return viewName;
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    public String getSql() {
        return sql;
    }
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public List<String> getViewNames() {
        return Arrays.asList(this.viewName);
    }
}
