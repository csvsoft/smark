package com.csvsoft.smark.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
    public static List<File> getFile(File rootDir) throws IOException{
        Path path =Paths.get(rootDir.toURI());
        List<File> pathList = Files.walk(path).filter(p -> p.toFile().isFile()).map(p->p.toFile()).collect(Collectors.toList());
        return pathList;
    }

    public static String getFileChecksumSHA( File file) throws IOException{
        MessageDigest shaDigest =null;
        try {
            MessageDigest.getInstance("SHA-1");
        }catch(NoSuchAlgorithmException ex){
            throw new RuntimeException("No such algorithm:SHA-1");
        }

        return getFileChecksum(shaDigest,file);
    }

    private static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
 }
