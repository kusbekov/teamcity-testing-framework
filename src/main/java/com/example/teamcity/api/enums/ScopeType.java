package com.example.teamcity.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScopeType {
    GLOBAL("g"),
    PROJECT("p"),
    BUILD_TYPE("bt");

    private final String prefix;

    public String withId(String id) {
        return prefix + ":" + id;
    }
}