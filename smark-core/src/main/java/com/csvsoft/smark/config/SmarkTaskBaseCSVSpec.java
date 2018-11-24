package com.csvsoft.smark.config;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SmarkTaskBaseCSVSpec extends SmarkTaskFileSpec {


    private String charset="UTF-8";
    private String header = "false";

    private String delimiter = ",";
    private String quoteChar = "\"";
    private  String escapeChar = "\\";


    private String dateFormat ;
    private String csvOptions;

    private String nullValue;

    private String viewName;
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }


    public String getNullValue() {
        return nullValue;
    }

    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }



    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }



    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getCsvOptions() {
        return csvOptions;
    }

    public Map<String,String> getCsvConf(){

        Map<String,String> map = new HashMap<>();
        map.put("header",String.valueOf(this.header));
        map.put("charset",this.charset);
        map.put("delimiter",String.valueOf(this.delimiter));
        map.put("quote",String.valueOf(this.quoteChar));
        map.put("escape",String.valueOf(this.escapeChar));
        if(StringUtils.isNotBlank(this.dateFormat)) {
            map.put("dateFormat", String.valueOf(this.dateFormat));
        }
        map.put("nullValue",this.nullValue);
        return map;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(String quoteChar) {
        this.quoteChar = quoteChar;
    }

    public String getEscapeChar() {
        return escapeChar;
    }

    public void setEscapeChar(String escapeChar) {
        this.escapeChar = escapeChar;
    }

    public void setCsvOptions(String csvOptions) {
        this.csvOptions = csvOptions;
    }

}
