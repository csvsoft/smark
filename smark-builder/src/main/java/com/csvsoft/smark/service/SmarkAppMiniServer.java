package com.csvsoft.smark.service;

import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.core.SmarkAppRunner;
import com.csvsoft.smark.entity.SparkCatalogContainer;
import com.csvsoft.smark.entity.SparkQueryResult;
import com.csvsoft.smark.exception.SmarkCompileFailureException;
import com.csvsoft.smark.exception.SmarkSparkServiceException;
import com.csvsoft.smark.sevice.ISmarkSparkService;
import com.csvsoft.smark.sevice.SparkCatalogProvider;
import com.csvsoft.smark.ui.SmarkAppBuilderUI;
import com.csvsoft.smark.util.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.JclUtils;
import scala.util.Try;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class SmarkAppMiniServer {

    Logger logger = LoggerFactory.getLogger(SmarkAppMiniServer.class);

    private ISmarkSparkService smarkSparkService;
    ClassLoader classLoader;

    private SmarkAppStatus status;

    private String appRootDir;

    private long lastCheckTime;
    private boolean stopFlag = false;
    private boolean serverBlock = false;

    private SmarkAppSpec appSpec;
    private SmarkAppBuilderUI builderUI;


    public SmarkAppMiniServer(SmarkAppSpec appSpec, SmarkAppBuilderUI builderUI) {
        this.appSpec = appSpec;
        this.builderUI = builderUI;
        this.appRootDir = appSpec.getCodeOutRootDir();
    }

    public SmarkAppMiniServer(SmarkAppSpec appSpec) {
        this.appSpec = appSpec;
        this.appRootDir = appSpec.getCodeOutRootDir();
        this.serverBlock = true;
    }

    public SmarkAppMiniServer(String appRootDir) {
        this.appRootDir = appRootDir;
    }



    public void start() {
        stopFlag = false;
        Thread t = getDaemonThread();
        t.start();
        if (serverBlock) {
            try {
                t.join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Thread getDaemonThread() {
        Thread t = new Thread() {
            @Override
            public void run() {
                logger.info("Start running server for " +(appSpec != null ? appSpec.getName() : ""));
                while (!stopFlag) {
                    try {
                        long lastModified = getLastModifiedTime();
                        if (lastModified > lastCheckTime) {
                            logger.info("Source file change detected.");
                            setStatus(SmarkAppStatus.COMPILING);
                            Try<String> stringTry = compileApp();
                            if (stringTry.isSuccess()) {
                                setStatus(SmarkAppStatus.COMPILED);
                                setStatus(SmarkAppStatus.INSTANTIATING);
                                initSmarkSparkService();
                                setStatus(SmarkAppStatus.READY);
                            } else {
                                setStatus(SmarkAppStatus.COMPILE_FAILED);
                            }

                            lastCheckTime = lastModified;
                        }

                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        return t;
    }


    public void stop() {
        stopFlag = true;
    }

    private long getLastModifiedTime() {
        String srcDir = appRootDir + "/src/main";
        //classesDir = "/tmp";
        File srcDirFile = new File(srcDir);
        if (!srcDirFile.exists()) {
            return -1;
        }
        List<File> files = new LinkedList<>();

        listFiles(srcDirFile, files);

        Path path = Paths.get(srcDir);

        Comparator<Long> comp = new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                if (o1 == o2) {
                    return 0;
                } else if (o1 > o2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        };
        try {
            Optional<Long> lastModified = Files.walk(path)
                    .filter(f -> f.toFile().isFile())
                    .map(f -> f.toFile().lastModified())
                    .max(comp);
            return lastModified.get();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to get last modified time.", ex);
        }


    }

    private void listFiles(File file, List<File> fileList) {
        try {
            if (!file.canRead()) {
                return;
            }
            if (file.isFile() && !file.getName().startsWith(".")) {
                fileList.add(file);
            } else if (file.isDirectory() && !file.isHidden()) {
                File[] files = file.listFiles();
                if (files == null) {
                    return;
                }
                for (File f : files) {
                    listFiles(f, fileList);
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
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

    private Try<String> compileApp() {
        logger.info("Compiling for " +( appSpec != null ? appSpec.getName() : ""));
        Try<String> stringTry = SmarkAppRunner.compileApp(appSpec);
        return stringTry;

    }


    public void initSmarkSparkService() {
         classLoader = getClassLoader();
        try {
            logger.info("Instantiating server for " + ((appSpec != null) ? appSpec.getName() : ""));
            Class clazz = classLoader.loadClass("com.csvsoft.smark.sevice.SmarkSparkService");
            Object serviceObj = clazz.newInstance();
            smarkSparkService = JclUtils.cast(serviceObj, ISmarkSparkService.class);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to instantiate te SmarkSparkService", ex);
        }


    }

    private ClassLoader getClassLoader() {
        String libDir = appRootDir + "/target/lib";
        String classesDir = appRootDir + "/target/classes";
        File[] files = new File(libDir).listFiles();
        File[] classPathFiles = Arrays.copyOf(files, files.length + 1);
        classPathFiles[classPathFiles.length - 1] = new File(classesDir);

        List<URL> urls = Arrays.stream(classPathFiles).map(f -> {
            try {
                return f.toURI().toURL();
            } catch (MalformedURLException ex) {
                return null;
            }
        }).collect(Collectors.toList());

        ClassLoader classLoader = new java.net.URLClassLoader(
                urls.toArray(new URL[0]), // Using current directory.
                ClassLoader.getSystemClassLoader().getParent());
        Thread.currentThread().setContextClassLoader(classLoader);
        return classLoader;
    }

    public void setStatus(SmarkAppStatus status) {
        synchronized (this) {
            this.status = status;
        }
       if(this.builderUI!=null) {
           showMessage(this.appSpec.getName() + " build status:" + status.value);
       }
    }

    public synchronized SmarkAppStatus getStatus() {
        return this.status;
    }

    private void checkIsReady() throws SmarkSparkServiceException{
        if(this.getStatus() == SmarkAppStatus.COMPILE_FAILED){
            throw new SmarkCompileFailureException("Compilation failed.");
        }
        if(this.getStatus() != SmarkAppStatus.READY){
            throw new SmarkCompileFailureException("Application is not ready, current status:"+this.getStatus().getValue());
        }
    }
    public SparkCatalogProvider executeSpec(long runId, String runTo, String specXML, String appProp) throws SmarkSparkServiceException {
        checkIsReady();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            logger.info(" mini server classLoader:"+classLoader.toString());
            logger.info("Running application for " + ((appSpec != null) ? appSpec.getName() : ""));
            //setStatus(SmarkAppStatus.RUNNING);
            byte[] bytes = smarkSparkService.executeSpecRemote(runId, runTo, specXML, appProp);
            //setStatus(SmarkAppStatus.READY);
            return SerializationUtils.deserialize(bytes, SparkCatalogProvider.class);
        } catch (Exception ex) {
            throw new SmarkSparkServiceException("Unable to execute spec:" + specXML, ex);
        }
    }

    public SparkCatalogProvider executeSpec(long runId, String runTo, SmarkAppSpec appSpec, String appProp) throws SmarkSparkServiceException {
        checkIsReady();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            logger.info(" mini server classLoader:"+classLoader.toString());
            //setStatus(SmarkAppStatus.RUNNING);
            logger.info("Running application for " + ((appSpec != null) ? appSpec.getName() : ""));
            byte[] specBytes = SerializationUtils.serialize(appSpec);
            byte[] bytes = smarkSparkService.executeSpecRemote(runId, runTo, specBytes, appProp);
            return SerializationUtils.deserialize(bytes, SparkCatalogProvider.class);
        } catch (Exception ex) {
            throw new SmarkSparkServiceException("Unable to execute spec:" + appSpec.getName()+",classLoader:"+classLoader.toString(), ex);
        }
    }

    public SparkQueryResult executeSQLRemote(String sql, int maxRows) throws SmarkSparkServiceException {
        checkIsReady();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            byte[] bytes = smarkSparkService.executeSQLRemote(sql, maxRows);
            return SerializationUtils.deserialize(bytes, SparkQueryResult.class);
        } catch (Exception ex) {
            throw new SmarkSparkServiceException("Unable to execute sql:" + sql, ex);
        }
    }


}
