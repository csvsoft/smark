package com.csvsoft.smark.service;

import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.core.SmarkApp;
import com.csvsoft.smark.core.util.SmarkAppSpecSerializer;
import com.csvsoft.smark.core.util.XmlUtils;
import com.csvsoft.smark.exception.SmarkSparkServiceException;
import com.csvsoft.smark.sevice.SparkCatalogProvider;
import org.junit.Test;

public class SmarkAppMiniServerTest {
   // @Test
    private void testMiniServer(){
        String rootDir = "/Users/zrq/workspace/vaddin/smark-northwind";
        String appProp = rootDir+"/src/main/resources/NorthWind.properties";
        String specXML = rootDir+"/src/main/resources/NorthWind.xml";
        SmarkAppSpec appSpec = XmlUtils.toObject(specXML, SmarkAppSpec.class);
        SmarkAppMiniServer server = new SmarkAppMiniServer(appSpec);

        server.start();

        server.initSmarkSparkService();
        //byte[] specBytes = SmarkAppSpecSerializer.serializeSmarkAppSpec(appSpec)
        try {
            SparkCatalogProvider sparkCatalogProvider = server.executeSpec(-1L, "5", specXML, appProp);
        } catch (SmarkSparkServiceException e) {
            e.printStackTrace();
        }


        //server.start();
    }
}
