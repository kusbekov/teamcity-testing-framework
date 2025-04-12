package com.example.teamcity.api.spec;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.User;
import io.restassured.authentication.BasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class Specifications {
    private static Specifications spec;

    private Specifications() {

    }

    public static Specifications getInstance() {
        if (spec == null) {
            spec = new Specifications();
        }
        return spec;
    }

    public RequestSpecBuilder reqBuilder() {
        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        return reqBuilder.setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setBaseUri("http://" + Config.getProperty("host"))
                .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
    }

    public RequestSpecification unauthSpec() {
        return reqBuilder().build();
    }

    public RequestSpecification authSpec(User user) {
        BasicAuthScheme basicAuthScheme= new BasicAuthScheme();
        basicAuthScheme.setUserName(user.getUser());
        basicAuthScheme.setPassword(user.getPassword());

        return reqBuilder().setAuth(basicAuthScheme).build();
    }
}
