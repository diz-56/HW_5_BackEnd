package DZ3;

import io.restassured.RestAssured;

import java.io.*;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import io.qameta.allure.restassured.*;


public abstract class BaseTest {
    static Properties properties = new Properties();
    static String token;
    static String username;

    @BeforeAll
    static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        getProperties();
        token = properties.getProperty("81ed217eee6d991be324edc8754a07e4ce686bb9");
        username = properties.getProperty("username");
    }
    private static void getProperties() {
        try (InputStream output = new FileInputStream("src/test/resources/application.properties")) {
            properties.load(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
