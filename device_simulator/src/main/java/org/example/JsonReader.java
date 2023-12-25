package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonReader {
    public JsonReader() {
    }

    public String readDeviceId(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + fileName);
            }

            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            AppConfig appConfig = objectMapper.readValue(content, AppConfig.class);
            return appConfig.getDeviceId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
