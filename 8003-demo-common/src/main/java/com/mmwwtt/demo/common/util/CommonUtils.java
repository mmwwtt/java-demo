package com.mmwwtt.demo.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {

    public static void println(Object... list) {
        log.info("{}",list);
    }

    /**
     * 将JSON串压缩，去掉空格/换行等
     * @param json
     * @return
     * @throws Exception
     */
    public static String compressJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Object jsonObject = mapper.readValue(json, Object.class);
        return mapper.writeValueAsString(jsonObject);
    }
}
