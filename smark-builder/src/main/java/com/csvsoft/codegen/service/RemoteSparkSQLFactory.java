package com.csvsoft.codegen.service;

import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.core.util.FileSystemUtils;
import com.csvsoft.smark.exception.SmarkRunTimeException;
import com.csvsoft.smark.exception.SmarkSparkServiceException;
import com.csvsoft.smark.service.SmarkAppMiniServer;
import com.csvsoft.smark.sevice.ICatalogueProvider;
import com.csvsoft.smark.sevice.SparkCatalogProvider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class RemoteSparkSQLFactory implements ISQLFactory {


    private SmarkAppMiniServer miniServer;
    private SmarkAppSpec  appSpec;
    private String runTo;

    public RemoteSparkSQLFactory(SmarkAppMiniServer miniServer, SmarkAppSpec appSpec, String runTo) {
        this.miniServer = miniServer;
        this.appSpec = appSpec;
        this.runTo = runTo;
    }

    @Override
    public ICatalogueProvider getCataLogueProvider() {
        File tempFile = FileSystemUtils.createTempFile("");
        String appProp = appSpec.getCodeOutRootDir()+"/src/main/resources"+appSpec.getName()+".properties";
        if(new File(appProp).exists()){
            FileSystemUtils.mergeFile(tempFile,new File(appProp));
        }
        FileSystemUtils.mergeFile(tempFile,new ByteArrayInputStream("debugMode=true".getBytes(StandardCharsets.UTF_8)));


        try {
            SparkCatalogProvider sparkCatalogProvider = miniServer.executeSpec(-1, runTo, appSpec, tempFile.getAbsolutePath());
            return sparkCatalogProvider;
        } catch (SmarkSparkServiceException e) {
            throw new SmarkRunTimeException("Unable to execute appSpec:"+appSpec.getName(),e);
        }


    }

    @Override
    public SQLDataProvider getSQLDataProvider() {
        RemoteSparkSQLService sqlService = new RemoteSparkSQLService(this.miniServer);
        return new SQLDataProvider(sqlService);
    }
}
