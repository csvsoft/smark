package com.csvsoft.smark.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class TemplateUtils {
    static {
        init();
        initClassPathVelocity();
    }

    private static VelocityEngine veClasspath;
    private static VelocityEngine veString;

    static void init() {
        veString = new VelocityEngine();
        veString.setProperty(Velocity.RESOURCE_LOADER, "string");
        veString.setProperty("string.resource.loader.class", StringResouceLoader.class.getName());

        veString.init();
    }

    static void initClassPathVelocity() {
        veClasspath = new VelocityEngine();
        veClasspath.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        veClasspath.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        veClasspath.init();
    }

    public static void genFile(String templateName, String outFileName, Map<String, ? extends Object> map) throws Exception {
        genFile(templateName, outFileName, map, true);
    }

    public static void genFile(String templateName, String outFileName, Map<String, ? extends Object> map, boolean override) throws Exception {

        Template template = veClasspath.getTemplate(templateName, "UTF-8");

        if (template == null) {
            throw new RuntimeException("Unable to find file in class path:" + templateName);
        }
        VelocityContext context = getVelocityContext(map);
        context.put("ctx", map);
        context.put("dateutil", new DateUtils());
        context.put("strutil", new StringUtils());

        context.put("fileutil", new FileToolUtil());
        File outFile = new File(outFileName);
        File parentDir = outFile.getParentFile();
        if (!parentDir.exists()) {
            FileUtils.forceMkdir(parentDir);
        }

        if (outFile.exists() && override == false) {
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        template.merge(context, writer);

        writer.flush();
        writer.close();
    }

    private static VelocityContext getVelocityContext(Map<String, ? extends Object> map) {
        VelocityContext context = new VelocityContext();
        for (Map.Entry<String, ? extends Object> e : map.entrySet()) {
            context.put(e.getKey(), e.getValue());
        }
        return context;

    }

    public static String merge(String templateStr, Map<String, ? extends Object> map) {
        Template template = veString.getTemplate(templateStr);
        VelocityContext context = getVelocityContext(map);

        /*
        context.put("dateutil", new DateUtils());
        context.put("strutil", new StringUtils());

        context.put("fileutil", new FileToolUtil());
        */


        StringWriter sw = new StringWriter(60 * 1024);
        template.merge(context, sw);
        sw.flush();
        String outputText = sw.toString();
        try {
            sw.close();
        } catch (IOException e) {
            // this would not happen
        }
        return outputText;


    }

    public static void genText(String templateName, String outFile, Map<String, Object> map) throws IOException {

        File templateFile = new File(templateName);
        VelocityEngine ve = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty(Velocity.RESOURCE_LOADER, "file");

        // Velocity.setProperty("classpath.resource.loader.description", "Velocity Classpath Resource Loader");
        // Velocity.setProperty("classpath.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        props.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        if (templateFile.getParent() == null) {
            props.setProperty("file.resource.loader.path", ".");
        } else {
            props.setProperty("file.resource.loader.path", templateFile.getParent());
        }
        ve.init(props);

        Template template = null;

        template = ve.getTemplate(templateFile.getName());
        VelocityContext context = new VelocityContext();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            context.put(e.getKey(), e.getValue());
        }
        context.put("ctx", map);
        context.put("dateutil", new DateUtils());
        context.put("strutil", new StringUtils());

        context.put("fileutil", new FileToolUtil());

        File outf = new File(outFile);
        File dir = outf.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        StringWriter sw = new StringWriter(60 * 1024);
        template.merge(context, sw);
        sw.flush();
        String outputText = sw.toString();
        sw.close();

        // if (outFile.toLowerCase().endsWith(".xml")) {
        // outputText = formatXML(outputText);
        // }

        FileWriter fw = new FileWriter(outf);
        String[] lines = StringUtils.split(outputText, "\n");
        boolean findFirstNonBlankLine = false;
        for (String line : lines) {
            if (!findFirstNonBlankLine && StringUtils.isBlank(line)) {
                continue;
            } else if (findFirstNonBlankLine) {
                fw.write(line);
                fw.write("\n");
            } else {
                findFirstNonBlankLine = true;
                fw.write(line);
                fw.write("\n");
            }
        }

        // FileWriter fw = new FileWriter(outf);
        // template.merge(context, fw);
        fw.close();
    }

}
