package com.csvsoft.smark.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SmarkTaskReadCSVSpec extends SmarkTaskBaseCSVSpec implements HaveViews{

    private String csvReadMode = "PERMISSIVE";
    private  Boolean inferSchema  = false;
    private String parserLib = "commons";
    private String comment = "#";


    public String getCsvReadMode() {
        return csvReadMode;
    }

    public void setCsvReadMode(String csvReadMode) {
        this.csvReadMode = csvReadMode;
    }

    public Boolean getInferSchema() {
        return inferSchema;
    }

    public void setInferSchema(Boolean inferSchema) {
        this.inferSchema = inferSchema;
    }

    public String getParserLib() {
        return parserLib;
    }

    public void setParserLib(String parserLib) {
        this.parserLib = parserLib;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }



    public List<String> getViewNames() {
        return Arrays.asList(this.getViewName());
    }


    public Map<String,String> getCsvConf(){
        Map<String, String> csvConf = super.getCsvConf();
        csvConf.put("mode",this.csvReadMode);
        csvConf.put("comment",this.comment);
        csvConf.put("parserLib",this.parserLib);
        csvConf.put("inferSchema",String.valueOf(inferSchema));
        return csvConf;
    }
}
