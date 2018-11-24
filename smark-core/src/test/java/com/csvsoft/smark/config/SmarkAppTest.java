package com.csvsoft.smark.config;

import com.csvsoft.smark.core.util.XmlUtils;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class SmarkAppTest {

    @Test
    public void generateNorwind(){
        SmarkAppSpec app = new SmarkAppSpec();
        //app.setPackageName("com.csvsoft.smark.example.northwind");
        app.setName("northwind");
        app.setLanguage("scala");
        app.setConfigOptions("dir.work = /tmp/northwind");
        app.setClassName("Northwind");
        app.setPackageName("com.csvsoft.smark.examples.northwind");
        app.setConfigOptions( "csvDir=/Users/zrq/workspace/vaddin/smark-example/src/main/resources/northwind");

        SmarkTaskReadCSVSpec categoryReadCSVTask = new SmarkTaskReadCSVSpec();
        categoryReadCSVTask.setFileName("${csvDir}/category.csv");
        categoryReadCSVTask.setViewName("category");
        categoryReadCSVTask.setName("Load category");
        categoryReadCSVTask.setClassName("LoadCategory");
        categoryReadCSVTask.setOrder(1);

        SmarkTaskReadCSVSpec productReadCSVTask = new SmarkTaskReadCSVSpec();
        productReadCSVTask.setFileName("${csvDir}/products.csv");
        productReadCSVTask.setViewName("products");
        productReadCSVTask.setName("Load products");
        productReadCSVTask.setClassName("LoadProduct");
        productReadCSVTask.setOrder(2);
        SQLViewPair p1 = new SQLViewPair("select * from products p inner join category c on p.categoryId = c.categoryId","products_category");

        SmarkTaskSQLSpec smarkSQL = new SmarkTaskSQLSpec();
        smarkSQL.setName("Join products and category");
        smarkSQL.setClassName("JoinProductsCategory");
        smarkSQL.setOrder(3);
        List<BaseSQLPair> sqlViewPairs = new LinkedList<>();
        sqlViewPairs.add(p1);
        smarkSQL.setSqlviewPairs(sqlViewPairs);

        SmarkTaskSaveJDBCSpec saveJDBCSpec = new SmarkTaskSaveJDBCSpec();
        saveJDBCSpec.setName("saveJDBC");

        List<SmarkTaskSpec> taskList = new LinkedList<>();
        taskList.add(categoryReadCSVTask);
        taskList.add(productReadCSVTask);
        taskList.add(smarkSQL);
        taskList.add(saveJDBCSpec);
        app.setSmarkTasks(taskList);

        XmlUtils.objectToXml(app,SmarkAppSpec.class, System.out);

    }

    @Test
    public void testMarshal(){
        SmarkAppSpec app = new SmarkAppSpec();
        app.setName("testapp");
        app.setLanguage("scala");
        app.setConfigOptions("dir.work = /tmp/dirwork");

        SmarkTaskReadCSVSpec readCSVTask = new SmarkTaskReadCSVSpec();
        readCSVTask.setFileName("test.csv");
        readCSVTask.setViewName("cars");
        readCSVTask.setCsvOptions("errorFile=bad.csv");

        SQLViewPair p1 = new SQLViewPair("select * from boxes","box");
        SQLViewPair p2 = new SQLViewPair("select * from cars","cars");

        SmarkTaskSQLSpec smarkSQL = new SmarkTaskSQLSpec();
        List<BaseSQLPair> sqlViewPairs = new LinkedList<>();
        sqlViewPairs.add(p1);
        sqlViewPairs.add(p2);
        smarkSQL.setSqlviewPairs(sqlViewPairs);


        SmarkTaskSaveJDBCSpec saveJDBCSpec = new SmarkTaskSaveJDBCSpec();
        saveJDBCSpec.setName("saveJDBC");

        List<SmarkTaskSpec> taskList = new LinkedList<>();
        taskList.add(readCSVTask);
        taskList.add(readCSVTask);
        taskList.add(smarkSQL);
        taskList.add(saveJDBCSpec);
        app.setSmarkTasks(taskList);

        try {

            JAXBContext context = JAXBContext.newInstance(SmarkAppSpec.class);
            Marshaller marshaller = context.createMarshaller();

            /** output the XML in pretty format */
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            /** display the output in the console */
            marshaller.marshal(app, System.out);

            /** put the XML to the file - will be used by the unmarshal example */
            marshaller.marshal(app, new File("/tmp/smarkapp.xml"));

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSmarkTaskCodeSpec(){

        try {
            SmarkTaskCodeSpec codeSpec = new SmarkTaskCodeSpec();
            codeSpec.setCode("testCode");
            codeSpec.setName("testCodeTaskName");
            codeSpec.setClassName("TaskCode");
            codeSpec.setOrder(1);
            codeSpec.setDesc("desc");

            File temp = File.createTempFile("testTaskletSpec", ".xml");

            OutputStream out = new FileOutputStream(temp);
            XmlUtils.objectToXml(codeSpec, SmarkTaskCodeSpec.class, out);
            IOUtils.closeQuietly(out);

            SmarkTaskCodeSpec unmarshaled = XmlUtils.toObject(temp, SmarkTaskCodeSpec.class);
            Assert.assertEquals(codeSpec.getCode(), unmarshaled.getCode());
        }catch(Exception ex){
            Assert.assertFalse("Unexpected error:"+ex.getMessage(),true);
        }


    }
    @Test
    public void testSmarkTaskReadCSVSpec(){

        try {
            SmarkTaskReadCSVSpec readCSVSpec = new SmarkTaskReadCSVSpec();
            //readCSVSpec.setCode("testCode");
            readCSVSpec.setName("testCodeTaskName");
            readCSVSpec.setClassName("ReadCSVTasklet1");
            readCSVSpec.setOrder(1);
            readCSVSpec.setDesc("desc");

            File temp = File.createTempFile("testTaskletSpec", ".xml");

            OutputStream out = new FileOutputStream(temp);
            XmlUtils.objectToXml(readCSVSpec, SmarkTaskReadCSVSpec.class, out);
            IOUtils.closeQuietly(out);

            SmarkTaskReadCSVSpec unmarshaled = XmlUtils.toObject(temp, SmarkTaskReadCSVSpec.class);
            Assert.assertEquals(readCSVSpec.getName(), unmarshaled.getName());
        }catch(Exception ex){
            Assert.assertFalse("Unexpected error:"+ex.getMessage(),true);
        }


    }
}
