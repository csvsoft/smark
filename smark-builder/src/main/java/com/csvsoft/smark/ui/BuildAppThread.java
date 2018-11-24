package com.csvsoft.smark.ui;

import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.core.SmarkAppRunner;
import com.csvsoft.smark.core.builder.SmarkAppBuilder;
import com.csvsoft.smark.exception.InvalidSmarkAppSpecException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.util.Try;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildAppThread implements Runnable {
    Logger logger = LoggerFactory.getLogger(BuildAppThread.class);
    SmarkAppBuilderUI builderUI;
    boolean stopSignal = false;

    private Map<SmarkAppSpec, Long> lastModifiedTimeMap = new HashMap<>();

    public BuildAppThread(SmarkAppBuilderUI builderUI) {
        this.builderUI = builderUI;
    }

    public void setStopSignal(boolean stopSignal) {
        this.stopSignal = stopSignal;
    }

    private boolean needToBuild(SmarkAppSpec appSpec) throws InvalidSmarkAppSpecException {
        long lastModified = builderUI.getSmarkAppSpecService().getAppSpecLastModifiedTime(builderUI.getUserCredential(), appSpec);
        Long cachedLastModified = lastModifiedTimeMap.get(appSpec);
        if (cachedLastModified == null) {
            lastModifiedTimeMap.put(appSpec, lastModified);
            return true;
        } else if (lastModified > cachedLastModified) {
            lastModifiedTimeMap.put(appSpec, lastModified);
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        logger.info("Starting the thread.");
        while(true && !stopSignal){
            buildSmarkApps();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void buildSmarkApps() {
        List<SmarkAppSpec> smarkAppSpecs = builderUI.getSmarkAppSpecService().listSmarkAppSpecs(builderUI.getUserCredential(), null);
        for (SmarkAppSpec appSpec : smarkAppSpecs) {
            //  logger.info("Starting build app:"+appSpec.getName());
            if(appSpec.getName().equals("App1")){
                continue;
            }

            boolean needBuild = false;
            try {
                needBuild = needToBuild(appSpec);
            } catch (InvalidSmarkAppSpecException e) {
                needBuild = false;
                showMessage(e.getMessage());
            }
            //logger.info("Need rebuild:"+appSpec.getName()+ "="+needBuild);
            if (!needBuild) {
                break;
            }

            logger.info("Regenerating application:"+appSpec.getName());
            SmarkAppBuilder.generateApp(appSpec);
            Try<String> stringTry = SmarkAppRunner.compileApp(appSpec);
            showMessage(appSpec.getName() + " start compiling...");
            if (stringTry.isSuccess()) {
                showMessage(appSpec.getName() + " compiled successfully.");
            } else {
                showMessage(appSpec.getName() + " compile failed.\n"+stringTry.failed().get().getMessage());
            }
            logger.info("Running application:"+appSpec.getName());
             stringTry = SmarkAppRunner.runSmarkWithMaven(appSpec);
            if (stringTry.isSuccess()) {
                showMessage(appSpec.getName() + " build successfully.");
            } else {
                showMessage(appSpec.getName() + " build failed.");
            }

        }
    }

    private void showMessage(String msg) {
        builderUI.access(new Runnable() {
            @Override
            public void run() {
                builderUI.showError(msg);
                builderUI.push();
            }
        });
    }
}
