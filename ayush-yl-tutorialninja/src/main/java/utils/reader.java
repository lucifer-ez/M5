package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads framework configuration from
 * src/test/resources/config/env.properties on the classpath.
 */
public class reader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream is = reader.class.getClassLoader()
                .getResourceAsStream("config/env.properties")) {
            if (is == null) {
                throw new RuntimeException("config/env.properties not found on classpath");
            }
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config/env.properties", e);
        }
    }

    public static String get(String key) {
        String systemProp = System.getProperty(key);
        if (systemProp != null && !systemProp.isEmpty()) {
            return systemProp;
        }
        return properties.getProperty(key);
    }
}
