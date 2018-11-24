package com.csvsoft.smark;

import com.csvsoft.smark.util.TemplateUtils;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import sun.nio.ch.IOUtil;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class TemplateUtilTest {

    @Test
    public void testMerge(){
        Map<String,Object> map = new HashMap<>();
        map.put("firstName","John");
        String out=TemplateUtils.merge("Hello ${firstName}",map);
        Assert.assertEquals("Hello John",out);
    }

    @Test
    public void testGenFile(){
        Map<String,Object> map = new HashMap<>();
        map.put("firstName","John");
        String outFile = "/tmp/testVelocity.txt";
        try {
           TemplateUtils.genFile("testTemplate.vm", outFile, map);
            String out =IOUtils.toString(new FileReader(new File(outFile)));
            Assert.assertEquals("Hello John",out);
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }
}
