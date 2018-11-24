package com.csvsoft.smark.service;

import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.ui.SmarkAppBuilderUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SmarkAppMiniServerRepo {
    Logger logger = LoggerFactory.getLogger(SmarkAppMiniServerRepo.class);

    private Map<SmarkAppSpec,SmarkAppMiniServer> serverMap = new HashMap<>(10);
    private SmarkAppBuilderUI builderUI;

    public SmarkAppMiniServerRepo(SmarkAppBuilderUI builderUI) {
        this.builderUI = builderUI;
    }

    public void addServer(SmarkAppSpec appSpec){
        if(serverMap.containsKey(appSpec)){
            return ;
        }
        logger.info("Trying to start mini server for :"+appSpec.getName());
        SmarkAppMiniServer miniServer = new SmarkAppMiniServer(appSpec,builderUI);
        serverMap.put(appSpec,miniServer);
        miniServer.start();
    }
    public SmarkAppMiniServer getServer(SmarkAppSpec appSpec){
        return serverMap.get(appSpec);
    }
    public void stopAllServers(){
        for(Map.Entry<SmarkAppSpec,SmarkAppMiniServer> e:serverMap.entrySet()){
           if( e.getValue()!=null){
               e.getValue().stop();
           }
        }
    }

}
