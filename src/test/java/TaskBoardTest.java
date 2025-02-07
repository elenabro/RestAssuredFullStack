import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskBoardTest {

    private static int taskId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://taskboard.portnov.com/api";
    }

    @Test
    @Order(1)
    public void testGetAllTasks() {
        Response response = given()
                .header("X-Api-Key", "myVerySecretKey")
                .when()
                .log().all()
                .get("/Task")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response();

        System.out.println(response.asString());
        List<Map<String, Object>> items = response.jsonPath().getList("$");
        for (Map<String, Object> task : items) {
            System.out.println(task.get("id"));
        }
    }

    @Test
    @Order(2)
    public void testPostTask() {
        String newTask = "{ \"id\": 0, " +
                "\"taskName\": \"Art020525\", " +
                "\"description\": \"string\", " +
                "\"dueDate\": \"2025-02-06T02:24:03.054Z\", " +
                "\"priority\": 0, " +
                "\"status\": \"string\", " +
                "\"author\": \"string\" " +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(newTask)
                .when()
                .post("/Task")
                .then()
                .statusCode(201)
                .extract().response();

        Map<String, Object> task = response.jsonPath().get();
        String id = task.get("id").toString();
        taskId = Integer.parseInt(id);
    }

    @Test
    @Order(3)
    public void updateTaskTest() {
        String updatedTask = "{ \"id\": "+taskId+", " +
                "\"taskName\": \"Art020525-updated\", " +
                "\"description\": \"string\", " +
                "\"dueDate\": \"2025-02-08T02:24:03.054Z\", " +
                "\"priority\": 0, " +
                "\"status\": \"string\", " +
                "\"author\": \"string\" " +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(updatedTask)
                .when()
                .put("/Task/" + taskId)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(4)
    public void testDeleteTask() {
        given()
                .when()
                .log().all()
                .delete("/Task/" + taskId)
                .then()
                .log().all()
                .statusCode(204);
    }
}
