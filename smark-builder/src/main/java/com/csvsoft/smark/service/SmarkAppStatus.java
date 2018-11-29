package com.csvsoft.smark.service;

public enum SmarkAppStatus {
    COMPILING("Compiling"),COMPILED("Compiled Successfully"),COMPILE_FAILED("Compile Failed"),
    INSTANTIATING("Instantiating"),READY("Ready"),RUNNING("Running"),UNKNOWN("Unknown");
    String value;
    SmarkAppStatus(String value){
        this.value = value;
    }
    public String getValue(){
        return this.value;
    }
}
