package com.lititi.exams.commons2.cache.serializer.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class FastJsonSerializer<T> implements RedisSerializer<T> {

    private final SerializerFeature[] defaultSerializeFeatures = { /*
                                                                    * SerializerFeature.WriteMapNullValue,
                                                                    * SerializerFeature.WriteNullListAsEmpty,
                                                                    * SerializerFeature.WriteNullStringAsEmpty,
                                                                    * SerializerFeature.QuoteFieldNames,
                                                                    * SerializerFeature.WriteDateUseDateFormat, SerializerFeature.PrettyFormat,
                                                                    * SerializerFeature.WriteEnumUsingToString,
                                                                    * SerializerFeature.DisableCircularReferenceDetect
                                                                    */};

    private final Feature[] defaultFeatures = {Feature.AutoCloseSource};

    /**
     * @see RedisSerializer#serialize(Object)
     */
    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONBytes(t, defaultSerializeFeatures);
    }

    /**
     * @see RedisSerializer#deserialize(byte[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return (T) JSON.parse(bytes, defaultFeatures);
    }

}
