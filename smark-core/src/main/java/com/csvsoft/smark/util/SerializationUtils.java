package com.csvsoft.smark.util;

import java.io.*;

public class SerializationUtils {

    public static <T> byte[] serialize(T o) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(o);
        byte[] bytes = bout.toByteArray();
        bout.close();

        return bytes;

    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        ObjectInputStream oin = new ObjectInputStream(bin);
        T o = (T) oin.readObject();
        bin.close();
        return o;
    }
}
