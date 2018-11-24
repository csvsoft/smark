package com.csvsoft.smark.builder;

import com.csvsoft.smark.config.SmarkTaskReadJDBCSpec;
import com.csvsoft.smark.service.sqlsuggestor.GenericJDBCCataLogProvider;
import com.csvsoft.smark.entity.IField;
import com.csvsoft.smark.util.JDBCConstants;
import com.csvsoft.smark.util.JDBCUtils;
import org.apache.spark.sql.SparkSession;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class GenericJDBCCataLogProviderTest {
    @Test
    public void testMetaData() throws SQLException, ClassNotFoundException {
        SmarkTaskReadJDBCSpec spec = new SmarkTaskReadJDBCSpec();
        spec.setJdbcurl("jdbc:mysql://localhost/timeinvoice?user=timeinvoice&password=tellmeok");
        spec.setDriverClass(JDBCConstants.DRIVER_CLASS_MYSQL);
        Connection con= JDBCUtils.getConnectin(spec);
        GenericJDBCCataLogProvider p = new GenericJDBCCataLogProvider(con);
        IField[] fields = p.getFields("INVOICe");
        Assert.assertNotEquals(0,fields.length);

    }


    @Test
    public void testSpark(){
        SparkSession sparkSession = SparkSession.builder().config("spark.driver.host","localhost").appName("smarkbuilder").master("local").getOrCreate();
    }
}
