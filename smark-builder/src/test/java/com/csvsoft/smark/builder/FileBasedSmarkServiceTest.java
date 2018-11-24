package com.csvsoft.smark.builder;


import com.csvsoft.smark.SmarkBuilderApplication;
import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.core.builder.SmarkAppBuilder;
import com.csvsoft.smark.entities.UserCredential;
import com.csvsoft.smark.service.FileBasedSmarkAppSpecService;
import com.csvsoft.smark.service.SmarkAppSpecService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//  import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

//@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SmarkBuilderApplication.class)
//@SpringApplicationConfiguration(classes = SmarkBuilderApplication.class)
@WebAppConfiguration
public class FileBasedSmarkServiceTest {

    @Autowired
    FileBasedSmarkAppSpecService specService;

    @Test
    public void testSave() {
        SmarkAppSpec spec = new SmarkAppSpec();
        spec.setName("testApp1");
        spec.setPackageName("packageName");
        spec.setClassName("className");
        UserCredential userC = new UserCredential();
        userC.setUserName("userName");

        try {
            specService.saveSmarkAppSpec(userC, spec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        List<SmarkAppSpec> smarkAppSpecs = specService.listSmarkAppSpecs(userC, "testApp1");
        Assert.assertEquals(1, smarkAppSpecs.size());

        List<SmarkAppSpec> smarkAppSpecsAll = specService.listSmarkAppSpecs(userC, null);
        Assert.assertEquals(1, smarkAppSpecsAll.size());

    }

    public void testListAppSpec() {

    }

    private UserCredential getCredentials() {
        //testUser1
        UserCredential userC = new UserCredential();
        userC.setUserName("testUser1");
        return userC;
    }

}