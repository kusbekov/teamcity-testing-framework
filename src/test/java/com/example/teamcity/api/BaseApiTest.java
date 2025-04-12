package com.example.teamcity.api;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.enums.RoleType;
import com.example.teamcity.api.enums.ScopeType;
import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.models.User;

import java.util.Arrays;
import java.util.List;

import static com.example.teamcity.api.generators.TestDataGenerator.generate;

public class BaseApiTest extends BaseTest {

    public User createUserWithRole(RoleType roleType, ScopeType scopeType, String projectId) {
        String scope = scopeType.withId(projectId);

        Role role = Role.builder()
                .roleId(roleType.getId())
                .scope(scope)
                .build();

        Roles roles = new Roles();
        roles.setRole(List.of(role));

        User user = generate(Arrays.asList(testData.getProject()), User.class, roles);

        superUserCheckRequests.getRequest(Endpoint.USERS).create(user);

        return user;
    }
}
