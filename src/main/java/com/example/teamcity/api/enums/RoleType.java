package com.example.teamcity.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    SYSTEM_ADMIN("SYSTEM_ADMIN"),
    PROJECT_ADMIN("PROJECT_ADMIN"),
    PROJECT_VIEWER("PROJECT_VIEWER"),
    AGENT_MANAGER("AGENT_MANAGER");

    private final String id;
}