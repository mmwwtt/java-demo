package com.lititi.exams.commons2.cache.serializer.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.ReflectionUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RedisSessionJavaSerializer implements RedisSerializer<LttRedisSessionInfoWrapper> {

    private static final String NOT_SERIALIZED = "___NOT_SERIALIZABLE_EXCEPTION___";

    private static final String SessionSerializationMetadata =
            "com.orangefunction.tomcat.redissessions.SessionSerializationMetadata";

    public byte[] attributesHashFrom(LttRedisSessionInfoWrapper redisSessionInfoWrapper) throws IOException {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.putAll(redisSessionInfoWrapper.getAttributes());
        byte[] serialized = null;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos))) {
            oos.writeUnshared(attributes);
            oos.flush();
            serialized = bos.toByteArray();
        }

        MessageDigest digester = null;
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("Unable to get MessageDigest instance for MD5");
        }
        return digester.digest(serialized);
    }

    @Override
    public byte[] serialize(LttRedisSessionInfoWrapper redisSession) throws SerializationException {

        Object sessionMetadata = null;
        try {
            Class<?> sessionMetadataClazz = Class.forName(SessionSerializationMetadata);
            sessionMetadata = sessionMetadataClazz.newInstance();
            Method method = ReflectionUtils.findMethod(sessionMetadataClazz, "setSessionAttributesHash",
                    new Class[]{byte[].class});
            ReflectionUtils.invokeMethod(method, sessionMetadata, attributesHashFrom(redisSession));
        } catch (Exception e) {
        }
        if (sessionMetadata == null) {
            return null;
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream stream = new ObjectOutputStream(new BufferedOutputStream(bos))) {
            writeInternal(redisSession, sessionMetadata, stream);
            stream.writeLong(redisSession.getCreationTime());
            stream.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("session 序列化失败,e=>{}", e.toString());
        }
        return null;
    }

    private void writeInternal(LttRedisSessionInfoWrapper redisSession, Object sessionMetadata,
                               ObjectOutputStream stream) throws IOException {
        stream.writeObject(sessionMetadata);
        stream.writeObject(Long.valueOf(redisSession.getCreationTime()));
        stream.writeObject(Long.valueOf(redisSession.getLastAccessedTime()));
        stream.writeObject(Integer.valueOf(redisSession.getMaxInactiveInterval()));
        stream.writeObject(Boolean.valueOf(redisSession.getIsNew()));
        stream.writeObject(Boolean.valueOf(redisSession.getIsValid()));
        stream.writeObject(Long.valueOf(redisSession.getThisAccessedTime()));
        stream.writeObject(redisSession.getId());
        String keys[] = redisSession.getAttributes().keySet().toArray(new String[]{});
        ArrayList<String> saveNames = new ArrayList<String>();
        ArrayList<Object> saveValues = new ArrayList<Object>();
        for (int i = 0; i < keys.length; i++) {
            Object value = redisSession.getAttributes().get(keys[i]);
            if (value == null)
                continue;
            else if (value instanceof Serializable) {
                saveNames.add(keys[i]);
                saveValues.add(value);
            }
        }

        int n = saveNames.size();
        stream.writeObject(Integer.valueOf(n));
        for (int i = 0; i < n; i++) {
            stream.writeObject(saveNames.get(i));
            try {
                stream.writeObject(saveValues.get(i));
            } catch (NotSerializableException e) {
                stream.writeObject(NOT_SERIALIZED);
            }
        }
    }

    /**
     * @see RedisSerializer#deserialize(byte[])
     */
    @Override
    public LttRedisSessionInfoWrapper deserialize(byte[] bytes) throws SerializationException {
        LttRedisSessionInfoWrapper redisSession = new LttRedisSessionInfoWrapper();
        try (BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(bytes));
             ObjectInputStream stream = new ObjectInputStream(bis)) {
            readDataInternal(redisSession, stream);
            stream.readLong();
        } catch (Exception e) {
            log.error("session 反序列化失败,e=>{}", e.toString());
        }
        return redisSession;
    }

    private void readDataInternal(LttRedisSessionInfoWrapper redisSession, ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        try {
            stream.readObject();
        } catch (Exception e) {
            log.error(e.toString());
        }
        Long creationTime = ((Long) stream.readObject()).longValue();
        redisSession.setCreationTime(creationTime);
        Long lastAccessedTime = ((Long) stream.readObject()).longValue();
        redisSession.setLastAccessedTime(lastAccessedTime);
        Integer maxInactiveInterval = ((Integer) stream.readObject()).intValue();
        redisSession.setMaxInactiveInterval(maxInactiveInterval);
        Boolean isNew = ((Boolean) stream.readObject()).booleanValue();
        redisSession.setIsNew(isNew);
        Boolean isValid = ((Boolean) stream.readObject()).booleanValue();
        redisSession.setIsValid(isValid);
        Long thisAccessedTime = ((Long) stream.readObject()).longValue();
        redisSession.setThisAccessedTime(thisAccessedTime);
        String id = (String) stream.readObject();
        redisSession.setId(id);
        int n = ((Integer) stream.readObject()).intValue();
        ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();
        for (int i = 0; i < n; i++) {
            String name = (String) stream.readObject();
            Object value = stream.readObject();
            if ((value instanceof String) && (value.equals(NOT_SERIALIZED)))
                continue;
            attributes.put(name, value);
        }

        redisSession.setAttributes(attributes);
        //
    }
}

