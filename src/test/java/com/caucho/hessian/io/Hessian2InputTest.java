package com.caucho.hessian.io;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Hessian2InputTest {

    SerializerFactory serializerFactory = new SerializerFactory();

    @Test
    public void testSerialize() {
        byte[] originBytes = generateLargeByteArray(64 * 1024);
        byte[] encodeBytes = hessianEncodeByte(originBytes);
        byte[] decodeBytes = hessianDecodeByte(encodeBytes);
        Assert.assertArrayEquals(originBytes, decodeBytes);
    }

    @Test
    public void testSerializeTime() {
        byte[] originBytes = generateLargeByteArray(1024 * 1024);
        byte[] encodeBytes = hessianEncodeByte(originBytes);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            hessianDecodeByte(encodeBytes);
        }
        long end = System.currentTimeMillis();
        System.out.println("cost:" + (end - start));
    }

    private static byte[] generateLargeByteArray(int size) {
        byte[] largeArray = new byte[size];
        for (int i = 0; i < size; i++) {
            largeArray[i] = (byte) (i % 256);
        }
        return largeArray;
    }

    private byte[] hessianEncodeByte(byte[] bytes) {
        serializerFactory.setAllowNonSerializable(true);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(os);
        output.setSerializerFactory(serializerFactory);
        try {
            output.writeObject(bytes);
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return os.toByteArray();
    }

    private byte[] hessianDecodeByte(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Hessian2Input input = new Hessian2Input(bis);
        input.setSerializerFactory(serializerFactory);
        byte[] decodeObject;
        try {
            decodeObject = (byte[]) input.readObject();
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return decodeObject;
    }

}