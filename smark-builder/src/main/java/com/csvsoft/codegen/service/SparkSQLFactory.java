package com.csvsoft.codegen.service;

import com.csvsoft.smark.sevice.ICatalogueProvider;
import com.csvsoft.smark.sevice.SparkCatalogProvider;
import org.apache.spark.sql.SparkSession;

public class SparkSQLFactory implements ISQLFactory {

    private SparkSession sparkSession;

    public SparkSQLFactory(SparkSession sparkSession){
        this.sparkSession = sparkSession;
    }
    @Override
    public ICatalogueProvider getCataLogueProvider() {
       return  new SparkCatalogProvider(this.sparkSession);
    }

    @Override
    public SQLDataProvider getSQLDataProvider() {
        SparkSQLService sqlService = new SparkSQLService(this.sparkSession);
        return new SQLDataProvider(sqlService);
    }
}
