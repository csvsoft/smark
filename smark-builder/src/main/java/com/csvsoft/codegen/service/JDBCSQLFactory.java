package com.csvsoft.codegen.service;

import com.csvsoft.smark.service.sqlsuggestor.GenericJDBCCataLogProvider;
import com.csvsoft.smark.sevice.ICatalogueProvider;

import java.sql.Connection;

public class JDBCSQLFactory implements ISQLFactory {

    private Connection connection;
    private ICatalogueProvider catalogueProvider;
    private String driverClass;

    public JDBCSQLFactory(Connection connection,String driverClass) {
        this.connection = connection;
        catalogueProvider = new GenericJDBCCataLogProvider(connection);
        this.driverClass = driverClass;
    }

    @Override
    public SQLDataProvider getSQLDataProvider() {
        JDBCSQLService sqlService = JDBCSQLServiceFactory.getJDBCSQLService(this.driverClass,this.connection);
        return new SQLDataProvider(sqlService);
    }

    @Override
    public ICatalogueProvider getCataLogueProvider() {
        return this.catalogueProvider;
    }


}
