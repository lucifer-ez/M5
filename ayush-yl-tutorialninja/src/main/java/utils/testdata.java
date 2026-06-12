package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads test data from src/test/resources/data/testdata.json
 * on the classpath. Returns a Jackson JsonNode tree which
 * DataProviders can iterate to build TestNG Object[][] datasets.
 */
public class testdata {

    private static JsonNode root;

    public static synchronized JsonNode get() {
        if (root == null) {
            try (InputStream is = testdata.class.getClassLoader()
                    .getResourceAsStream("data/testdata.json")) {
                if (is == null) {
                    throw new RuntimeException("data/testdata.json not found on classpath");
                }
                root = new ObjectMapper().readTree(is);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load data/testdata.json", e);
            }
        }
        return root;
    }
}
