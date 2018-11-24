package com.csvsoft.smark.config;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Properties;

public class PropertiesBuilderTest {

    @Test
    public void testEmbeddedVar(){
        String homeDir = System.getenv("HOME");
        String propStr = "vaddinHome=$HOME/vaddin\nvaddinSrc=${vaddinHome}/src";
        Properties props = PropertiesBuilder.getProps(propStr);
        String actualVaddin = props.getProperty("vaddinHome");
        String vaddinSrc = props.getProperty("vaddinSrc");
        Assert.assertEquals(homeDir + "/vaddin",actualVaddin);
        Assert.assertEquals(homeDir+"/vaddin/src",vaddinSrc);
    }
}
