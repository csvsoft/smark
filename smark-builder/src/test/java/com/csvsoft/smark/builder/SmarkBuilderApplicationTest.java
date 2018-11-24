package com.csvsoft.smark.builder;


import com.csvsoft.smark.SmarkBuilderApplication;
import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.entities.UserCredential;
import com.csvsoft.smark.service.FileBasedSmarkAppSpecService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;
//  import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SmarkBuilderApplication.class)
//@SpringApplicationConfiguration(classes = SmarkBuilderApplication.class)
@WebAppConfiguration
public class SmarkBuilderApplicationTest {



    @Test
    public void testSave() {

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