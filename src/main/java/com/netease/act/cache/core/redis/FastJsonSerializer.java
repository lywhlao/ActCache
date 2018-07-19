package com.netease.act.cache.core.redis;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

public class FastJsonSerializer implements RedisSerializer {


    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (o == null) {
            return new byte[0];
        }
        return JSONObject.toJSONString(o).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        return JSONObject.parse(str);
    }
}
