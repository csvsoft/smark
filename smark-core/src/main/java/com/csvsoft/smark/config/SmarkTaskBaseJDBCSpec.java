package com.csvsoft.smark.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SmarkTaskBaseJDBCSpec extends SmarkTaskSpec {

    private String jdbcurl;
    private String jdbcOpitons;
    private String driverClass;
    private String sessionInitStatement;
    private int numPartitions;

    public int getNumPartitions() {
        return numPartitions;
    }

    public void setNumPartitions(int numPartitions) {
        this.numPartitions = numPartitions;
    }

    public String getSessionInitStatement() {
        return sessionInitStatement;
    }

    public void setSessionInitStatement(String sessionInitStatement) {
        this.sessionInitStatement = sessionInitStatement;
    }

    public String getDriverClass() {
        return driverClass;
    }
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }
    public Properties getJdbcOptionProps(){
        return PropertiesBuilder.getProps(this.jdbcOpitons);
    }
    public String getJdbcurl() {
        return jdbcurl;
    }
    public void setJdbcurl(String jdbcurl) {
        this.jdbcurl = jdbcurl;
    }
    public String getJdbcOpitons() {
        return jdbcOpitons;
    }
    public void setJdbcOpitons(String jdbcOpitons) {
        this.jdbcOpitons = jdbcOpitons;
    }
}
