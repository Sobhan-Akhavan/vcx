package ir.vcx.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import ir.vcx.exception.runtime.JsonProcessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Slf4j
@Component
public class JsonUtil {

    private static final ObjectMapper mapper;
    private static final ObjectMapper iso8601DateFormatMapper;

    static {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(
                        new SimpleModule().addSerializer(Double.class, new JsonSerializer<Double>() {
                            @Override
                            public void serialize(Double value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                                jsonGenerator.writeNumber(new BigDecimal(value.toString()).toPlainString());
                            }
                        })
                );

        iso8601DateFormatMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.INDENT_OUTPUT, false)
                .setDateFormat(new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601))
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(
                        new SimpleModule().addSerializer(Double.class, new JsonSerializer<Double>() {
                            @Override
                            public void serialize(Double value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                                jsonGenerator.writeNumber(new BigDecimal(value.toString()).toPlainString());
                            }
                        })
                );
    }

    public static String getStringJson(Object obj) {
        if (obj == null)
            return null;
        try {
            mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("unsuccessful parsing json", e);
            throw new JsonProcessException(e);
        }
    }

    public static String getWithIso8601DateFormatJson(Object obj) {
        try {
            return iso8601DateFormatMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("unsuccessful parsing json", e);
            throw new JsonProcessException(e);
        }
    }

    public static String getIntentStringJson(Object obj) {
        try {
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("unsuccessful parsing json", e);
            throw new JsonProcessException(e);
        }
    }

    public static Map<?, ?> getMap(Object o) {
        return mapper.convertValue(o, Map.class);
    }

    public static String getWithNoTimestampJson(Object obj) {
        try {
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("unsuccessful parsing json", e);
            throw new JsonProcessException(e);
        }
    }

    public static <T> T getObject(byte[] json, Class<T> classOfT) {
        try {
            String content = new String(json, StandardCharsets.UTF_8);
            log.debug("byte content: " + content);
            return mapper.readValue(content, classOfT);
        } catch (IOException e) {
            throw new JsonProcessException(e, classOfT.getSimpleName());
        }
    }

    public static <T> T getObject(String json, Class<T> classOfT) {
        try {
            return json == null ? null : mapper.readValue(json, classOfT);
        } catch (IOException e) {
            throw new JsonProcessException(e, classOfT.getSimpleName());
        }
    }

    public static <T> T getObject(Map map, Class<T> classOfT) {
        try {
            return mapper.convertValue(map, classOfT);
        } catch (IllegalArgumentException e) {
            throw new JsonProcessException(e, classOfT.getSimpleName());
        }
    }

    public static <T> T getObject(JsonNode json, Class<T> classOfT) {
        try {
            return json == null ? null : mapper.convertValue(json, classOfT);
        } catch (IllegalArgumentException e) {
            throw new JsonProcessException(e, classOfT.getSimpleName());
        }
    }

    public static <T> T getObject(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            String typeName = typeReference.getType().getTypeName();
            throw new JsonProcessException(e, typeName.substring(typeName.lastIndexOf(".") + 1));
        }
    }

    public static JsonNode getJsonObject(String json) {
        try {
            return json == null ? null : mapper.readTree(json);
        } catch (IOException e) {
            throw new JsonProcessException(e);
        }
    }

    public static ObjectNode getJsonFromMap(Map<?, ?> map) {
        return map == null ? null : mapper.valueToTree(map);
    }

    public static ObjectNode getEmptyObject() {
        return mapper.createObjectNode();
    }
}
