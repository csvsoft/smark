package com.csvsoft.smark;

public enum CSVReadMode {
    PERMISSIVE("PERMISSIVE","tries to parse all lines: nulls are inserted for missing tokens and extra tokens are ignored."),
    DROPMALFORMED("DROPMALFORMED","drops lines which have fewer or more tokens than expected or tokens which do not match the schema"),
    FAILFAST("FAILFAST","aborts with a RuntimeException if encounters any malformed line");
    String value;
    String desc;
     CSVReadMode(String value,String desc){
        this.value = value;
        this.desc = desc;
    }
    public String getValue(){
        return this.value;
    }
    public String getDesc(){
         return this.desc;
    }
}
