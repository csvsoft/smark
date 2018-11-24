package com.csvsoft.smark.service;

import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.entities.UserCredential;
import com.csvsoft.smark.exception.InvalidSmarkAppSpecException;

import java.util.List;

public interface SmarkAppSpecService {
    public SmarkAppSpec getNewSmarkAppSpec(UserCredential userCredential);
    public void saveSmarkAppSpec(UserCredential userCredential,SmarkAppSpec smarkAppSpec) throws InvalidSmarkAppSpecException;
    public SmarkAppSpec loadSmarkAppSpec(UserCredential userCredential, String smarkAppId);
    public List<SmarkAppSpec> listSmarkAppSpecs(UserCredential userCredential,String name);
    public void deleteSmarkAppSpec(UserCredential userCredential,SmarkAppSpec smarkAppSpec);
    public long getAppSpecLastModifiedTime(UserCredential userCredential, SmarkAppSpec smarkAppSpec) throws InvalidSmarkAppSpecException ;
    public  UserCredential getDefaultUserCredential();
    }
