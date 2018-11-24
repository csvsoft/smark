package com.csvsoft.codegen.service;


import com.csvsoft.smark.sevice.ICatalogueProvider;

public interface ISQLFactory {

    public ICatalogueProvider getCataLogueProvider();
    public SQLDataProvider getSQLDataProvider();

}
