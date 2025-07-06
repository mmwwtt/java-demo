package com.lititi.exams.commons2.cache.serializer.redis;

import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LttJDKSerializer implements RedisSerializer<Object> {
    static final byte[] EMPTY_ARRAY = new byte[0];

    public static boolean isGzip(byte[] bytes) {
        if (bytes.length < 2) {
            return false;
        }
        int ss = (bytes[0] & 0xff) | ((bytes[1] & 0xff) << 8);
        if (ss == GZIPInputStream.GZIP_MAGIC) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            Object obj = null;
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            if (isGzip(bytes)) {
                GZIPInputStream gzipIn = new GZIPInputStream(bais);
                ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
                obj = objectIn.readObject();
                objectIn.close();
            } else {
                ObjectInputStream objectIn = new ObjectInputStream(bais);
                obj = objectIn.readObject();
                objectIn.close();
            }
            return obj;
        } catch (Throwable ex) {
            throw new SerializationFailedException(
                    "Failed to serialize object using " + getClass().getSimpleName(), ex);
        }
    }

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return EMPTY_ARRAY;
        }
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            if (!(object instanceof Serializable)) {
                throw new IllegalArgumentException(getClass().getSimpleName() + " requires a Serializable payload "
                        + "but received an object of type [" + object.getClass().getName() + "]");
            }
            GZIPOutputStream gzipOut = new GZIPOutputStream(byteStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOut);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            return byteStream.toByteArray();
        } catch (Throwable ex) {
            throw new SerializationFailedException(
                    "Failed to serialize object using " + getClass().getSimpleName(), ex);
        }
    }

    static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }
}
