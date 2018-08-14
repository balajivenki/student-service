package com.qualys.meetup.management;

public enum HealthStatus {
    UP("UP"),
    DOWN("DOWN"),
    WRONG_HEALTH_CHECK_CONFIGURATION("WRONG_HEALTH_CHECK_CONFIGURATION"),
    ELASTIC_TEMPLATE_MISSING("ELASTIC_TEMPLATE_MISSING");;

    private String value;

    HealthStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
