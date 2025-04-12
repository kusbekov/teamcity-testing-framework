package com.example.teamcity.api;

import com.example.teamcity.api.enums.RoleType;
import com.example.teamcity.api.enums.ScopeType;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.TestData;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("errors[0].message",
                        Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template"
                                .formatted(testData.getBuildType().getId()))
                );
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        var projectAdminUser = createUserWithRole(RoleType.PROJECT_ADMIN, ScopeType.PROJECT, testData.getProject().getId());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(projectAdminUser));

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {

        TestData testData1 = generate();
        TestData testData2 = generate();

        superUserCheckRequests.getRequest(PROJECTS).create(testData1.getProject());
        superUserCheckRequests.getRequest(PROJECTS).create(testData2.getProject());

        var projectAdminUser1 = createUserWithRole(RoleType.PROJECT_ADMIN, ScopeType.PROJECT, testData1.getProject().getId());
        var projectAdminUser2 = createUserWithRole(RoleType.PROJECT_ADMIN, ScopeType.PROJECT, testData2.getProject().getId());

        var forbiddenBuildType = testData1.getBuildType();

        new UncheckedBase(Specifications.authSpec(projectAdminUser2), BUILD_TYPES)
                .create(forbiddenBuildType)
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body("errors[0].message", Matchers.containsString(
                        "You do not have enough permissions to edit project with id: %s\nAccess denied. Check the user has enough permissions to perform the operation."
                                .formatted(testData1.getProject().getId())
                ));
    }
}