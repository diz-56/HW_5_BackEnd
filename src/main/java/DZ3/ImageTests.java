package DZ3;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class ImageTests extends BaseTest {
    static String encodedFile;
    String uploadedImageId;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent("src/test/resources/s-l300.jpg");
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
    }

    @Test
    void getNonExistingImageTest() {
        given()
                .header("Authorization", token)
                .when()
                .get("https://api.imgur.com/3/image/this_image_does_not_exist")
                .prettyPeek()
                .then()
                .statusCode(404);
    }

    @Test
    void uploadFileTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .formParam("title", "ImageTitle")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }
    @Test
    void uploadFileImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/test/resources/s-l300.jpg"))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }
    @Test
    void uploadJPEGFormatTest() {
        String encodedFile;
        byte[] byteArray = getFileContent("src/test/resources/jake.jpeg");
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadGIFFormatTest() {
        String encodedFile;
        byte[] byteArray = getFileContent("src/test/resources/adventure-time-jake.gif");
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .body("data.type", equalTo("image/gif"))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadPNGFormatTest() {
        String encodedFile;
        byte[] byteArray = getFileContent("src/test/resources/jake.png");
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .body("data.type", equalTo("image/png"))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadFile1x1pixelTest() {
        String encodedFile;
        byte[] byteArray = getFileContent("src/test/resources/1x1-0000ff7f.png");
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .body("data.type", equalTo("image/jpeg"))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadBMPFormatTest() {
        String encodedFile;
        byte[] byteArray = getFileContent("src/test/resources/jake.bmp");
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .body("data.type", equalTo("image/png")) //Imgur transforms .bmp to .png
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }
    @Test
    void uploadAndFavoriteFileTest() {
        String encodedFile;
        byte[] byteArray = getFileContent("src/test/resources/jake.jpeg");
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        String imageHash = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");

        given()
                .headers("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", imageHash)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
    @AfterEach
    void tearDown() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image{deleteHash}", "testprogmath", uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
    private byte[] getFileContent(String PATH_TO_IMAGE) {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
