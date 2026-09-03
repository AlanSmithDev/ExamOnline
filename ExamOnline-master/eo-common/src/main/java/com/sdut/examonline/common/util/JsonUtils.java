package com.sdut.examonline.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chen
 * @since 2022/3/6 20:42
 */
public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String toJson(T o) throws JsonProcessingException {
        return MAPPER.writeValueAsString(o);
    }

    public static <T> T from(String json, Class<T> clazz) throws IOException {
        return MAPPER.readValue(json, clazz);
    }

    public static <T> T from(String json, TypeReference<T> valueTypeRef) throws IOException {
        return MAPPER.readValue(json, valueTypeRef);
    }

    public static <O, I> O from(String json, Class<O> oCLass, Class<I> iClass) throws IOException {
        return MAPPER.readValue(json, MAPPER.getTypeFactory().constructParametricType(oCLass, iClass));
    }

    public static <T> List<T> toList(String json, Class<T> clazz) throws IOException {
        return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
    }

}
