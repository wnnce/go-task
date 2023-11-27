package ink.task.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: lisang
 * @DateTime: 2023-11-13 19:00:34
 * @Description: Json工具类
 */
public final class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //不序列化null值
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 反序列化Json字符串
     * @param json Json字符串
     * @param clazz 需要返回类的Class类型
     * @return 返回指定的发序列化类型
     * @param <T> 泛型 指定的反序列化类型
     */
    public static <T> T form(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        }catch (JsonProcessingException ex) {
            logger.error("Json反序列化失败，错误信息：{}", ex.getMessage());
        }
        return null;
    }

    /**
     * 序列化对象为Json字符串
     * @param value 需要序列化的对象
     * @return 返回序列化后的Json字符串
     */
    public static String to(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        }catch (JsonProcessingException ex) {
            logger.error("Json序列化失败，错误信息：{}", ex.getMessage());
        }
        return null;
    }
}
