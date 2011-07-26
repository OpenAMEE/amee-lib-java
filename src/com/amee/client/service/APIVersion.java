package com.amee.client.service;

import java.util.Map;
import java.util.HashMap;

public enum APIVersion {

    ONE ("1.0"), TWO ("2.0");

    private String version;
    
    private static final Map<String, APIVersion> stringToEnum = new HashMap<String, APIVersion>();
    static {
        for (APIVersion apiVersion : values()) {
            stringToEnum.put(apiVersion.toString(), apiVersion);
        }
    }

    APIVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return version;
    }

    public static APIVersion fromString(String version) {
        return stringToEnum.get(version);
    }

}
