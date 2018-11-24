package com.csvsoft.smark.config;


import com.csvsoft.smark.core.util.XmlUtils;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.*;
import java.util.UUID;

@XmlSeeAlso({SmarkTaskReadCSVSpec.class
        , SmarkTaskReadJDBCSpec.class
        ,SmarkTaskReadAvroSpec.class
        ,SmarkTaskReadJsonSpec.class
        ,SmarkTaskReadORCSpec.class

        ,SmarkTaskSaveAvroSpec.class
        ,SmarkTaskSaveJsonSpec.class
        ,SmarkTaskSaveORCSpec.class
        ,SmarkTaskSaveCSVSpec.class

        ,SmarkTaskReadParquetSpec.class
        , SmarkTaskCodeSpec.class
        , SmarkTaskSQLSpec.class
        , SmarkTaskSaveCSVSpec.class
        ,SmarkTaskSaveJDBCSpec.class})
public class SmarkTaskSpec implements Serializable{
    @XmlAttribute
    private String smarkTaskId;
    private String name;
    private String desc;
    private int order;
    private String codeType;
    private String className;
    private Boolean isSavePoint = false;


    public SmarkTaskSpec() {
        this.smarkTaskId = UUID.randomUUID().toString();
    }

    public void generateNewId(){
        this.smarkTaskId = UUID.randomUUID().toString();
    }

    public String getSmarkTaskId() {
        return smarkTaskId;
    }

    public Boolean getSavePoint() {
        return isSavePoint;
    }

    public void setSavePoint(Boolean savePoint) {
        isSavePoint = savePoint;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public String getPreTaskCode() {
        return preTaskCode;
    }

    public void setPreTaskCode(String preTaskCode) {
        this.preTaskCode = preTaskCode;
    }

    public String getPostTaskCode() {
        return postTaskCode;
    }

    public void setPostTaskCode(String postTaskCode) {
        this.postTaskCode = postTaskCode;
    }

    private String preTaskCode;
    private String postTaskCode;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    private String myGetClassCodeOutDir(SmarkAppSpec appSpec, String generatedDir) {
        String gDir = StringUtils.isBlank(generatedDir) ? "" : File.separator + generatedDir;
        String base = StringUtils.isBlank(generatedDir) ? "" : "Base";
        return StringUtils.join(new String[]{appSpec.getCodeOutRootDir(), File.separator, appSpec.getPackageName().replace('.', '/'), gDir, File.separator, base, this.className, ".scala"});
    }

    public String getBaseClassCodeOutDir(SmarkAppSpec appSpec) {
        return myGetClassCodeOutDir(appSpec, "generated");
    }

    public String getClassCodeOutDir(SmarkAppSpec appSpec) {
        return myGetClassCodeOutDir(appSpec, "");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SmarkTaskSpec)) {
            return false;
        }
        SmarkTaskSpec other = (SmarkTaskSpec) obj;
        return this.smarkTaskId.equalsIgnoreCase(other.smarkTaskId);
    }

   public SmarkTaskSpec clone(){
       ByteArrayOutputStream bout = new ByteArrayOutputStream();
       try {
           ObjectOutputStream out = new ObjectOutputStream(bout);
           out.writeObject(this);
           byte[] bytes = bout.toByteArray();
           out.close();
           bout.close();

           ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
           ObjectInputStream objectInputStream = new ObjectInputStream(bin);
           SmarkTaskSpec taskSpec = (SmarkTaskSpec)objectInputStream.readObject();
           return taskSpec;

       }catch(Exception ex){
           throw new RuntimeException("Unable to clone object:"+this.getClass().getName(),ex);
       }

   }
}
