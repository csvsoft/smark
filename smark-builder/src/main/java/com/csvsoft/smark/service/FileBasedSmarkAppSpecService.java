package com.csvsoft.smark.service;

import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.core.SmarkApp;
import com.csvsoft.smark.core.builder.SmarkAppBuilder;
import com.csvsoft.smark.core.util.XmlUtils;
import com.csvsoft.smark.entities.UserCredential;
import com.csvsoft.smark.entities.UserRole;
import com.csvsoft.smark.exception.InvalidSmarkAppSpecException;
import com.vaadin.spring.annotation.UIScope;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hdfs.server.common.IncorrectVersionException;
import org.json4s.Xml;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@UIScope
public class FileBasedSmarkAppSpecService implements  SmarkAppSpecService,InitializingBean {

    @Value("${smarkapp.repo.dir}")
    private String repoDir;

    @Value("${smarkapp.defaultUser}")
    private String defaultUser;
    @Value("${smarkapp.defaultUserRole}")
    private String defaultUserRole;



    public UserCredential getDefaultUserCredential(){
        UserCredential userCredential = new UserCredential();
        if(defaultUser == null){
            throw new RuntimeException("Not default user specified");
        }
        userCredential.setUserId(defaultUser);
        userCredential.setUserName(defaultUser);

        UserRole userRole = new UserRole(this.defaultUserRole);
        userCredential.addUserRole(userRole);
        return userCredential;
    }

    @Override
    public SmarkAppSpec getNewSmarkAppSpec(UserCredential userCredential) {
        return new SmarkAppSpec();
    }

    @Override
    public void deleteSmarkAppSpec(UserCredential userCredential, SmarkAppSpec smarkAppSpec) {
        String specXml = getFileName(userCredential,smarkAppSpec.getName());
        File specFile = new File(specXml);
        if(specFile.exists()){
            specFile.delete();
        }
    }

    public long getAppSpecLastModifiedTime(UserCredential userCredential, SmarkAppSpec smarkAppSpec) throws InvalidSmarkAppSpecException {
        String specXml = getFileName(userCredential, smarkAppSpec.getName());
        File specFile = new File(specXml);
        if(!specFile.exists()){
            throw new InvalidSmarkAppSpecException("No smark spec found with name:"+smarkAppSpec.getName());
        }
        return  specFile.lastModified();
    }

        @Override
    public void saveSmarkAppSpec(UserCredential userCredential, SmarkAppSpec smarkAppSpec) throws InvalidSmarkAppSpecException {
        String specXml = getFileName(userCredential,smarkAppSpec.getName());
        File specFile = new File(specXml);
        createParentDirSafe(specFile);
        try {
            smarkAppSpec.adjustTasksOrder();
            smarkAppSpec.validate();
            OutputStream outputStream = new FileOutputStream(specFile);
            XmlUtils.objectToXml(smarkAppSpec, SmarkAppSpec.class, outputStream);

            IOUtils.closeQuietly(outputStream);
            SmarkAppBuilder.generateApp(smarkAppSpec);
        }catch(FileNotFoundException ex){
            throw new RuntimeException("file not found:",ex);
        }

    }

    private void createParentDirSafe(File file){
        File parentDir = file.getParentFile();
        if(!parentDir.exists()){
            parentDir.mkdirs();
        }
    }


    private String getFileName(UserCredential userCredential, String appName){

        String appId = StringUtils.replace(appName," ","_");

        String userName = userCredential.getUserName();
        String[] pathEles = new String[]{this.repoDir ,userName,appId};
        String filePath = StringUtils.join(pathEles ,File.separator);
        return filePath + ".xml";

    }

    private String getUserDir(UserCredential userCredential){
        String userName = userCredential.getUserName();
        String[] pathEles = new String[]{this.repoDir ,userName};
        String filePath = StringUtils.join(pathEles ,File.separator);
        createParentDirSafe(new File(filePath));
        return filePath;
    }

    @Override
    public SmarkAppSpec loadSmarkAppSpec(UserCredential userCredential, String smarkAppName) {
        File specXml = new File(getFileName(userCredential,smarkAppName));
        SmarkAppSpec spec = null;
        if(specXml.exists()){
            spec = XmlUtils.toObject(specXml,SmarkAppSpec.class);
        }
        return spec;

    }

    @Override
    public List<SmarkAppSpec> listSmarkAppSpecs(UserCredential userCredential, String name) {
        File userDir = new File(getUserDir(userCredential));
        Collection<File> specFiles =FileUtils.listFiles(userDir,new String[]{"xml"},true);
        List<SmarkAppSpec> smarkAppSpecs = specFiles.stream()
                .filter(file->{
                    boolean shouldBeIn = false;
                    if(StringUtils.isBlank(name)) {
                        shouldBeIn = true;
                    }else if(file.getName().toUpperCase().contains(name.toUpperCase())){
                        shouldBeIn = true;
                    }
                    return shouldBeIn;
                }).map(file -> XmlUtils.toObject(file, SmarkAppSpec.class)).collect(Collectors.toList());
        return smarkAppSpecs;
    }

    @Override
    public void afterPropertiesSet() throws Exception{

        File file = new File(this.repoDir);
        if(!file.exists()){
            file.mkdirs();
            return ;
        }
        if(!file.isDirectory()){
            throw new RuntimeException(repoDir + "is not a directory");
        }
        if(!file.canWrite()){
            throw new RuntimeException(repoDir + " not writable.");
        }

    }
}
