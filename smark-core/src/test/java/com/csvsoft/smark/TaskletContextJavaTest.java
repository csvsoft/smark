package com.csvsoft.smark;

import com.csvsoft.smark.core.SmarkAppConfig;
import com.csvsoft.smark.core.TaskletContext;
import junit.framework.Assert;
import org.junit.Test;
import scala.None;
import scala.Option;

import java.util.Properties;

public class TaskletContextJavaTest {

    @Test
    public void testOptionString(){
        SmarkAppConfig config= new SmarkAppConfig(new Properties(),-1);
        TaskletContext ctx = new TaskletContext(config);
        ctx.addTaskVar("str1","str1Value");
        Option<String> str = ctx.getString("str");
        //Assert.assertEquals(None,str);
        if( str.isDefined()){
            System.out.println(str);
        }else{
            System.out.println("Null found");
        }


    }

}
