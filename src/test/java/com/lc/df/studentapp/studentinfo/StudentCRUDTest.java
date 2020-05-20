package com.lc.df.studentapp.studentinfo;

import com.lc.df.studentapp.model.StudentPojo;
import com.lc.df.studentapp.testbase.TestBase;
import com.lc.df.studentapp.utils.TestUtils;
import io.restassured.http.ContentType;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Title;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.hasValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Ankita
 */
@RunWith(SerenityRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StudentCRUDTest extends TestBase {

    static String firstName = "Sonu" + TestUtils.getRandomValue();
    static String lastName = "Patel" + TestUtils.getRandomValue();
    static String email = TestUtils.getRandomValue() + "abc123@gmail.com";
    static String programme = "Medicine";
    static int studentId;

    //CRUD TEST (Create,Read , Update and Delete)

    @Title("This test will create a new student record")
    @Test
    public void test001() {
        List<String> courses = new ArrayList<>();
        courses.add("Biology");
        courses.add("Chemistry");

        StudentPojo studentPojo = new StudentPojo();
        studentPojo.setFirstName(firstName);
        studentPojo.setLastName(lastName);
        studentPojo.setEmail(email);
        studentPojo.setProgramme(programme);
        studentPojo.setCourses(courses);

        SerenityRest.rest()
                .given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(studentPojo)
                .post()
                .then()
                .log().all().statusCode(201);


    }

    @Title("Verify if the student was added to the list")
    @Test
    public void test002() {
        String str1 = "findAll{it.firstName=='";
        String str2 = "'}.get(0)";

        HashMap<String, Object> value = SerenityRest.rest().given()
                .when()
                .get("/list")
                .then()
                .statusCode(200)
                .extract()
                .path(str1 + firstName + str2);
        assertThat(value, hasValue(firstName));
        studentId = (int) value.get("id");
        System.out.println("The student ID for the new student is : " +studentId);

    }

    @Title("Update the student information and verify the information")
    @Test
    public void test003(){

        List<String> courses = new ArrayList<>();
        courses.add("Biology");
        courses.add("Chemistry");

        firstName = firstName + "_Amend";
        lastName = lastName + "_Updated";

        StudentPojo studentPojo = new StudentPojo();
        studentPojo.setFirstName(firstName);
        studentPojo.setLastName(lastName);
        studentPojo.setEmail(email);
        studentPojo.setProgramme(programme);
        studentPojo.setCourses(courses);

        SerenityRest.rest()
                .given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(studentPojo)
                .put("/" +studentId)
                .then()
                .log().all().statusCode(200);

        String str1 = "findAll{it.firstName=='";
        String str2 = "'}.get(0)";

        HashMap<String, Object> value = SerenityRest.rest().given()
                .when()
                .get("/list")
                .then()
                .statusCode(200)
                .extract()
                .path(str1 + firstName + str2);
        assertThat(value, hasValue(firstName));
        assertThat(value, hasValue(lastName));
        System.out.println(value);

    }

    @Title("Delete the student record and verify if the student is deleted")
    @Test
    public void test04() {
        SerenityRest.rest()
                .given()
                .when()
                .delete("/"+studentId)
                .then()
                .statusCode(204);

        SerenityRest.rest()
                .given()
                .when()
                .get("/"+studentId)
                .then().statusCode(404);

    }


}
